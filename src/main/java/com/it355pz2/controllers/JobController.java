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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private JobService jobService;
    private ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<JobResponse>> getJobs() {
        return ResponseEntity.ok(jobService.getJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
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
        var application = applicationService.createApplication(principal.getUser().getId(), id, dto.getDescription());
        if (application == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(application);
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(Authentication authentication, @RequestBody JobDto dto) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        var job = jobService.createJob(principal.getUser().getId(), dto);
        if (job == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(job);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteJob(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.deleteJob(id));
    }


}