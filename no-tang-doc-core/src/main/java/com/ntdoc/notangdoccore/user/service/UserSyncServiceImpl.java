package com.ntdoc.notangdoccore.user.service;

import com.ntdoc.notangdoccore.user.repository.UserRepository;
import com.ntdoc.notangdoccore.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSyncServiceImpl {
    private final UserRepository userRepository;

    @Transactional
    public User ensureLocalUser(String username, String email){
        Optional<User> existing = userRepository.findByUsername(username);
        if(existing.isPresent()){
            User user = existing.get();
            boolean changed = false;
            if (email != null && (user.getEmail() == null || !email.equals(user.getEmail()))){
                user.setEmail(email);
                changed = true;
            }
            return changed ? userRepository.save(user) : user;
        }
        User user = User.builder().username(username).email(email).build();
        return userRepository.save(user);
    }

    @Transactional
    public User ensureFromJwt(Jwt jwt) {
        String sub = jwt.getSubject();
        if (sub == null || sub.isBlank()) {
            throw new IllegalArgumentException("JWT Subject is required");
        }

        String preferredUsername = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        String username = firstNonBlank(preferredUsername, email, sub);

        Optional<User> existing = userRepository.findByKcUserId(sub);
        if (existing.isPresent()) {
            User user = existing.get();
            boolean changed = false;

            if (!Objects.equals(user.getUsername(), username)) {
                user.setUsername(username);
                changed = true;
            }
            if (!Objects.equals(user.getEmail(), email)) {
                user.setEmail(email);
                changed = true;
            }
            return changed ? userRepository.save(user) : user;
        }

        try {
            User user = User.builder()
                    .kcUserId(sub)
                    .username(username)
                    .email(email)
                    .build();
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return userRepository.findByKcUserId(sub).orElseThrow(() -> e);
        }
    }

    private String firstNonBlank(String... candidates) {
        for (String c : candidates) {
            if (c != null && !c.isBlank()) return c;
        }
        return null;
    }
}
