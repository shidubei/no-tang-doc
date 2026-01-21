package com.ntdoc.notangdoccore.user.repository;

import com.ntdoc.notangdoccore.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByKcUserId(String kcUserId);

    Optional<User> findByEmail(String email);
}
