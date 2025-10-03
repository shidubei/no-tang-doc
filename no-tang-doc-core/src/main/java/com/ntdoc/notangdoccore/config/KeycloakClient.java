package com.ntdoc.notangdoccore.config;

import com.ntdoc.notangdoccore.dto.keycloak.AuthExchangeRequest;
import com.ntdoc.notangdoccore.exception.KeycloakClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.endpoint.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * KeycloakClient: uses Spring Security token response clients for authorization_code + refresh_token.
 * NOTE: Because this is a front-end (SPA) driven code+PKCE flow, we manually build the AuthorizationExchange.
 */
@Component
public class KeycloakClient {
    private final ClientRegistration clientRegistration;
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authCodeClient;
    private final OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> refreshTokenClient;
    private final WebClient webClient; // still used for logout

    private final String issuerUri;
    private final String clientId;
    private final String clientSecret;

    public KeycloakClient(ClientRegistrationRepository registrations,
                          @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String clientId,
                          @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}") String clientSecret,
                          @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}") String issuerUri) {
        ClientRegistration reg = registrations.findByRegistrationId("keycloak");
        if (reg == null) {
            throw new IllegalStateException("Client registration 'keycloak' not found");
        }
        this.clientRegistration = reg;
        this.authCodeClient = new RestClientAuthorizationCodeTokenResponseClient();
        this.refreshTokenClient = new RestClientRefreshTokenTokenResponseClient();
        this.webClient = WebClient.builder().build();
        this.issuerUri = issuerUri;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /** Exchange authorization code + PKCE verifier for tokens. */
    public Map<String,Object> exchangeCode(AuthExchangeRequest req) {
        Objects.requireNonNull(req, "request must not be null");
        if (req.getCode() == null || req.getCodeVerifier() == null || req.getRedirectUri() == null) {
            throw new KeycloakClientException("invalid_request","Missing code/code_verifier/redirect_uri",400, Map.of());
        }
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .clientId(clientRegistration.getClientId())
                .redirectUri(req.getRedirectUri())
                .scopes(clientRegistration.getScopes())
                .state("ignored")
                .attributes(attrs -> attrs.put(PkceParameterNames.CODE_VERIFIER, req.getCodeVerifier()))
                .build();
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success(req.getCode())
                .redirectUri(req.getRedirectUri())
                .state("ignored")
                .build();
        OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
        OAuth2AuthorizationCodeGrantRequest grantRequest = new OAuth2AuthorizationCodeGrantRequest(clientRegistration, exchange);
        try {
            OAuth2AccessTokenResponse tokenResponse = authCodeClient.getTokenResponse(grantRequest);
            return toMap(tokenResponse);
        } catch (OAuth2AuthorizationException ex) {
            throw wrap("authorization_code", ex);
        }
    }

    /** Refresh access token using refresh_token. */
    public Map<String,Object> refresh(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            throw new KeycloakClientException("invalid_request","Missing refresh_token",400, Map.of());
        }
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(refreshTokenValue, Instant.now().minusSeconds(60));
        // Dummy expired access token (value unused by request builder but required by constructor)
        OAuth2AccessToken dummyAccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                "expired", Instant.now().minusSeconds(3600), Instant.now().minusSeconds(300));
        OAuth2RefreshTokenGrantRequest refreshRequest = new OAuth2RefreshTokenGrantRequest(
                clientRegistration, dummyAccessToken, refreshToken);
        try {
            OAuth2AccessTokenResponse tokenResponse = refreshTokenClient.getTokenResponse(refreshRequest);
            return toMap(tokenResponse);
        } catch (OAuth2AuthorizationException ex) {
            throw wrap("refresh_token", ex);
        }
    }

    /** Logout (end session) */
    public void logout(String refreshToken, String idToken) {
        String logoutEndpoint = issuerUri + "/protocol/openid-connect/logout";
        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }
        if (refreshToken != null && !refreshToken.isBlank()) {
            form.add("refresh_token", refreshToken);
        } else if (idToken != null && !idToken.isBlank()) {
            form.add("id_token_hint", idToken);
        }
        webClient.post().uri(logoutEndpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .toBodilessEntity()
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    private Map<String,Object> toMap(OAuth2AccessTokenResponse resp) {
        Map<String,Object> map = new HashMap<>();
        map.put("access_token", resp.getAccessToken().getTokenValue());
        map.put("token_type", resp.getAccessToken().getTokenType().getValue());
        if (resp.getRefreshToken() != null) {
            map.put("refresh_token", resp.getRefreshToken().getTokenValue());
        }
        if (resp.getAccessToken().getExpiresAt() != null && resp.getAccessToken().getIssuedAt() != null) {
            long expiresIn = Duration.between(resp.getAccessToken().getIssuedAt(), resp.getAccessToken().getExpiresAt()).getSeconds();
            map.put("expires_in", expiresIn);
        }
        map.putAll(resp.getAdditionalParameters());
        return map;
    }

    private KeycloakClientException wrap(String grantType, OAuth2AuthorizationException ex) {
        OAuth2Error err = ex.getError();
        String errorCode = err != null ? err.getErrorCode() : "oauth2_error";
        String desc = err != null ? err.getDescription() : ex.getMessage();
        return new KeycloakClientException(errorCode, desc, 400, Map.of("grant_type", grantType));
    }
}