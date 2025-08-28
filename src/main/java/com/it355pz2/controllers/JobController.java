package com.it355pz2.controllers;

import com.it355pz2.dto.ApplicationDto;
import com.it355pz2.dto.ApplicationResponse;
import com.it355pz2.dto.JobDto;
import com.it355pz2.dto.JobResponse;
import com.it355pz2.entity.enums.ApplicationStatus;
import com.it355pz2.security.UserPrincipal;
import com.it355pz2.services.ApplicationService;
import com.it355pz2.services.JobService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import com.it355pz2.repository.PaymentTypeRepository;
import com.it355pz2.repository.JobRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@AllArgsConstructor
@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job Management", description = "Job posting and management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class JobController {

    private JobService jobService;
    private ApplicationService applicationService;
    private PaymentTypeRepository paymentTypeRepository;
    private JobRepository jobRepository;

    @GetMapping
    @Operation(
            summary = "Get All Jobs",
            description = "Retrieves a paginated list of all available jobs with optional filtering"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Jobs retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JobResponse.PaginatedJobResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<JobResponse.PaginatedJobResponse> getJobs(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "6") int size,
            @Parameter(description = "Filter jobs by location") @RequestParam(required = false) String location,
            @Parameter(description = "Search jobs by keyword") @RequestParam(required = false) String search,
            @Parameter(description = "Filter jobs by type (FULL_TIME, PART_TIME, CONTRACT, TEMPORARY)") @RequestParam(required = false) String type) {

        try {

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);

            org.springframework.data.domain.Page<JobResponse> jobsPage = jobService.getJobsPaginated(pageable, location, search, type);

            JobResponse.PaginatedJobResponse response = JobResponse.createPaginatedResponse(jobsPage);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Job by ID",
            description = "Retrieves a specific job by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Job retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JobResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Job not found"
            )
    })
    public ResponseEntity<JobResponse> getJob(@Parameter(description = "Job ID") @PathVariable Long id) {
        var job = jobService.getJob(id);
        if (job == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(job);
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStatus(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationsByStatus(id, ApplicationStatus.pending));
    }

    @PostMapping("/{id}/applications")
    public ResponseEntity<ApplicationResponse> createApplication(Authentication authentication, @PathVariable Long id, @RequestBody ApplicationDto dto) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        var application = applicationService.createApplication(principal.getUser().getId(), id, dto.getMessage());
        if (application == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(application);
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    @Operation(
            summary = "Create New Job",
            description = "Creates a new job posting (Employers only)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Job created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JobResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid job data or user not authorized"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<JobResponse> createJob(
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(description = "Job details") @RequestBody JobDto dto) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            var job = jobService.createJob(principal.getUser().getId(), dto);
            if (job == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(Authentication authentication, @PathVariable Long id, @RequestBody JobDto dto) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            if (!principal.getUser().getUserType().equals(com.it355pz2.entity.enums.UserType.employer)) {
                return ResponseEntity.status(403).build();
            }

            if (!jobService.isJobOwner(id, principal.getUser().getId())) {
                return ResponseEntity.status(403).build();
            }

            var jobUpdate = new com.it355pz2.dto.JobUpdate();
            jobUpdate.setTitle(dto.getTitle());
            jobUpdate.setDescription(dto.getDescription());
            jobUpdate.setDateFrom(dto.getDateFrom());
            jobUpdate.setDateTo(dto.getDateTo());
            jobUpdate.setStatus(dto.getStatus());
            jobUpdate.setType(dto.getType());
            jobUpdate.setLocation(dto.getLocation());
            jobUpdate.setPaymentAmount(dto.getPaymentAmount());
            jobUpdate.setUrgent(dto.getUrgent());
            jobUpdate.setCategoryIds(dto.getCategoryIds());

            var paymentType = paymentTypeRepository.findById(dto.getPaymentTypeId()).orElse(null);
            if (paymentType != null) {
                jobUpdate.setPaymentType(paymentType.getTitle());
            }

            var job = jobService.updateJob(id, jobUpdate);
            if (job == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteJob(Authentication authentication, @PathVariable Long id) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            if (!principal.getUser().getUserType().equals(com.it355pz2.entity.enums.UserType.employer)) {
                return ResponseEntity.status(403).build();
            }

            if (!jobService.isJobOwner(id, principal.getUser().getId())) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(jobService.deleteJob(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


}