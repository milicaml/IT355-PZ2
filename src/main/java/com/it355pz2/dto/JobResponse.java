package com.it355pz2.dto;

import com.it355pz2.entity.Job;
import com.it355pz2.entity.enums.JobStatusType;
import com.it355pz2.entity.enums.JobType;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Data
public class JobResponse {
    private Long id;
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
    private List<String> categories;

    public JobResponse(Job job) {
        this.id = job.getId();
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
        
        // Initialize empty categories list
        this.categories = new ArrayList<>();
    }

    // Static method to create paginated response wrapper
    public static PaginatedJobResponse createPaginatedResponse(Page<JobResponse> page) {
        PaginatedJobResponse response = new PaginatedJobResponse();
        response.setContent(page.getContent());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setCurrentPage(page.getNumber());
        response.setSize(page.getSize());
        return response;
    }

    // Inner class for paginated response
    @Data
    public static class PaginatedJobResponse {
        private List<JobResponse> content;
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private int size;
    }
}