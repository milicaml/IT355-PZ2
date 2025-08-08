package com.it355pz2.controllers;

import com.it355pz2.dto.ApplicationResponse;
import com.it355pz2.entity.enums.ApplicationStatus;
import com.it355pz2.services.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    private ApplicationService applicationService;

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplication(@PathVariable Long id) {
        var application = applicationService.getApplication(id);
        if (application == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(application);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id, @RequestParam ApplicationStatus status) {
        var application = applicationService.updateStatus(id, status);
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
