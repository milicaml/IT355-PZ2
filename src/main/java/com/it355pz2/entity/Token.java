package com.it355pz2.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tokens")
public class Token {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private String expiresAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;
}