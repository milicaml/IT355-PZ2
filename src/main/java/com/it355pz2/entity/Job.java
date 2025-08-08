package com.it355pz2.entity;

import com.it355pz2.entity.enums.JobStatusType;
import com.it355pz2.entity.enums.JobType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "jobs")
public class Job {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createByUser;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "date_from", nullable = false)
    private String dateFrom;

    @Column(name = "date_to", nullable = false)
    private String dateTo;

    @Column(name = "job_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatusType statusType;

    @Column(name = "job_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobType type;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "payment_amount", nullable = false)
    private double paymentAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_type_id", nullable = false)
    private PaymentType paymentType;

    @Column(name = "urgent", nullable = false)
    private int urgent;

    @Column(name = "is_archived")
    private boolean isArchived;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobCategory> jobCategories = new ArrayList<>();


}
