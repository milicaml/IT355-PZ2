package com.it355pz2.dto;

import com.it355pz2.entity.Job;
import com.it355pz2.entity.enums.JobStatusType;
import com.it355pz2.entity.enums.JobType;
import lombok.Data;

@Data
public class JobResponse {
    private Long createdBy;
    private String createdByName;
    private String title;
    private String description;
    private String dateFrom;
    private String dateTo;
    private JobStatusType status;
    private JobType type;
    private String location;
    private double paymentAmount;
    private String paymentType;
    private int urgent;

    public JobResponse(Job job) {
        this.createdBy = job.getCreateByUser().getId();
        this.createdByName = job.getCreateByUser().getFullName();
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.dateFrom = job.getDateFrom();
        this.dateTo = job.getDateTo();
        this.status = job.getStatusType();
        this.type = job.getType();
        this.location = job.getLocation();
        this.paymentAmount = job.getPaymentAmount();
        this.paymentType = job.getPaymentType().getTitle();
        this.urgent = job.getUrgent();
    }
}