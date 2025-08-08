package com.it355pz2.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "payment_types")
public class PaymentType {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;
}