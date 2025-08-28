package com.it355pz2.controllers;

import com.it355pz2.dto.ApplicationResponse;
import com.it355pz2.dto.ApplicationDto;
import com.it355pz2.dto.StatusUpdateRequest;
import com.it355pz2.entity.enums.ApplicationStatus;
import com.it355pz2.entity.enums.UserType;
import com.it355pz2.security.UserPrincipal;
import com.it355pz2.services.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getUserApplications(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<ApplicationResponse> applications = applicationService.getApplicationsByUser(principal.getUser().getId());
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/employer")
    public ResponseEntity<List<ApplicationResponse>> getJobApplicationsForEmployer(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<ApplicationResponse> applications = applicationService.getApplicationsForEmployer(principal.getUser().getId());
        return ResponseEntity.ok(applications);
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> applyForJob(@RequestBody ApplicationDto applicationDto, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        if (principal.getUser().getUserType() != UserType.freelancer) {
            return ResponseEntity.status(403).body(null);
        }

        ApplicationResponse application = applicationService.createApplication(
                principal.getUser().getId(),
                applicationDto.getJobId(),
                applicationDto.getMessage()
        );

        if (application == null) {
            if (applicationService.hasUserAppliedForJob(principal.getUser().getId(), applicationDto.getJobId())) {
                return ResponseEntity.status(409).body(null); // 409 Conflict for duplicate
            }
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(application);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplication(@PathVariable Long id) {
        var application = applicationService.getApplication(id);
        if (application == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(application);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        var application = applicationService.updateStatus(id, request.getStatus());
        if (application == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(application);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable Long id) {
        try {
            applicationService.deleteApplication(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Application deleted");
    }


}
