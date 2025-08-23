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
            System.out.println("JobController - getJobs called with page=" + page + ", size=" + size + ", location=" + location + ", search=" + search + ", type=" + type);
            
            // Create Pageable object for pagination
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            
            // Fetch paginated jobs from the database
            org.springframework.data.domain.Page<JobResponse> jobsPage = jobService.getJobsPaginated(pageable, location, search, type);
            System.out.println("JobController - Found " + jobsPage.getContent().size() + " jobs on page " + page + " of " + jobsPage.getTotalPages());
            
            // Convert to the format expected by frontend
            JobResponse.PaginatedJobResponse response = JobResponse.createPaginatedResponse(jobsPage);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("JobController - Exception in getJobs: " + e.getMessage());
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
            System.out.println("JobController - Authentication: " + authentication);
            System.out.println("JobController - Principal: " + authentication.getPrincipal());
            System.out.println("JobController - JobDto: " + dto);
            
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            System.out.println("JobController - User ID: " + principal.getUser().getId());
            System.out.println("JobController - User: " + principal.getUser());
            System.out.println("JobController - User Type: " + principal.getUser().getUserType());
            
            var job = jobService.createJob(principal.getUser().getId(), dto);
            if (job == null) {
                System.out.println("JobController - Job creation failed, returning bad request");
                return ResponseEntity.badRequest().build();
            }
            System.out.println("JobController - Job created successfully: " + job);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            System.out.println("JobController - Exception during job creation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(Authentication authentication, @PathVariable Long id, @RequestBody JobDto dto) {
        try {
            System.out.println("JobController - Update Job - Authentication: " + authentication);
            System.out.println("JobController - Update Job - Job ID: " + id);
            System.out.println("JobController - Update Job - JobDto: " + dto);
            
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            System.out.println("JobController - Update Job - User ID: " + principal.getUser().getId());
            System.out.println("JobController - Update Job - User Type: " + principal.getUser().getUserType());
            
            // Manual authorization check
            if (!principal.getUser().getUserType().equals(com.it355pz2.entity.enums.UserType.employer)) {
                System.out.println("JobController - Access denied: User is not an employer");
                return ResponseEntity.status(403).build();
            }
            
            if (!jobService.isJobOwner(id, principal.getUser().getId())) {
                System.out.println("JobController - Access denied: User is not the job owner");
                return ResponseEntity.status(403).build();
            }
            
            // Convert JobDto to JobUpdate
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
            
            // Get payment type name from ID
            var paymentType = paymentTypeRepository.findById(dto.getPaymentTypeId()).orElse(null);
            if (paymentType != null) {
                jobUpdate.setPaymentType(paymentType.getTitle());
            }
            
            var job = jobService.updateJob(id, jobUpdate);
            if (job == null) {
                System.out.println("JobController - Job update failed, returning bad request");
                return ResponseEntity.badRequest().build();
            }
            System.out.println("JobController - Job updated successfully: " + job);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            System.out.println("JobController - Exception during job update: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteJob(Authentication authentication, @PathVariable Long id) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            System.out.println("JobController - Delete Job - User ID: " + principal.getUser().getId());
            System.out.println("JobController - Delete Job - User Type: " + principal.getUser().getUserType());
            
            // Manual authorization check
            if (!principal.getUser().getUserType().equals(com.it355pz2.entity.enums.UserType.employer)) {
                System.out.println("JobController - Access denied: User is not an employer");
                return ResponseEntity.status(403).build();
            }
            
            if (!jobService.isJobOwner(id, principal.getUser().getId())) {
                System.out.println("JobController - Access denied: User is not the job owner");
                return ResponseEntity.status(403).build();
            }
            
            return ResponseEntity.ok(jobService.deleteJob(id));
        } catch (Exception e) {
            System.out.println("JobController - Exception during job deletion: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


}