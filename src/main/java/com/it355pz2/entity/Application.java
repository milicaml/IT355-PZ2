package com.it355pz2.entity;

import com.it355pz2.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "applications")
public class Application {
    @Id
    @Column(name = "id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id",nullable = false)
    private Job job;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "is_deleted",nullable = false)
    private boolean isDeleted;

    @Column(name = "created_at",nullable = false)
    private String createdAt;

    @Column(name = "updated_at",nullable = false)
    private String updatedAt;
}
