package com.ntdoc.notangdoccore.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user",
        indexes = {@Index(name = "uq_app_user_username", columnList = "username", unique = true)},
        uniqueConstraints = {@UniqueConstraint(name = "uq_app_user_kc_user_id",columnNames="kc_user_id")}
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="kc_user_id",nullable = false,length = 64)
    private String kcUserId;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(length = 128)
    private String email;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

}
