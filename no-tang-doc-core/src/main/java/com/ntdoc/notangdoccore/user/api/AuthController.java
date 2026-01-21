package com.ntdoc.notangdoccore.user.api;

import com.ntdoc.notangdoccore.config.AuthProperties;
import com.ntdoc.notangdoccore.config.KeycloakClient;
import com.ntdoc.notangdoccore.dto.keycloak.AuthExchangeRequest;
import com.ntdoc.notangdoccore.user.api.dto.RefreshRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 用户认证注册以及登录
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final KeycloakClient keycloakClient;
    private final AuthProperties authProperties;

    @PostMapping("/exchange")
    public ResponseEntity<?> exchange(@RequestBody AuthExchangeRequest req) {
        try{
            if (req.getRedirectUri() == null || authProperties.getAllowedRedirectUris() == null){
                authProperties.getAllowedRedirectUris().stream().noneMatch(r -> r.equalsIgnoreCase(req.getRedirectUri()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success",false,"error","redirect_not_allow"));
            }
            if(req.getCode()==null || req.getCodeVerifier()==null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success",false,"error","missing_code_or_verifier"));
            }
            Map<String,Object> token = keycloakClient.exchangeCode(req);
            return ResponseEntity.ok(token);
        }catch(Exception e){
            log.error("Exchange failure",e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success",false,"error","invalid_request"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {

    }


}
