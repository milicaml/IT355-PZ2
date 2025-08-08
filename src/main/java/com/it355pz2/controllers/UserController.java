package com.it355pz2.controllers;

import com.it355pz2.dto.ApplicationResponse;
import com.it355pz2.dto.JobResponse;
import com.it355pz2.dto.UserResponse;
import com.it355pz2.dto.UserUpdate;
import com.it355pz2.services.ApplicationService;
import com.it355pz2.services.JobService;
import com.it355pz2.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private ApplicationService applicationService;
    private JobService jobService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUser(id);
        if (user == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdate updatedUser) {
        try {
            UserResponse user = userService.updateUser(id, updatedUser);
            if (user == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("User deleted");
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByUser(@PathVariable Long id) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByUser(id);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}/jobs")
    public ResponseEntity<List<JobResponse>> getJobsByUser(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobsByCreator(id));
    }
}
