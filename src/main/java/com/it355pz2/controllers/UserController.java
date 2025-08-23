package com.it355pz2.controllers;

import com.it355pz2.dto.ApplicationResponse;
import com.it355pz2.dto.JobResponse;
import com.it355pz2.dto.UserResponse;
import com.it355pz2.dto.UserUpdate;
import com.it355pz2.dto.SkillResponse;
import com.it355pz2.dto.AddUserSkillRequest;
import com.it355pz2.dto.UpdateUserSkillRequest;
import com.it355pz2.entity.enums.UserType;
import com.it355pz2.security.UserPrincipal;
import com.it355pz2.services.ApplicationService;
import com.it355pz2.services.JobService;
import com.it355pz2.services.UserService;
import com.it355pz2.services.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User profile and management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private UserService userService;
    private ApplicationService applicationService;
    private JobService jobService;
    private SkillService skillService;

    @GetMapping("/profile")
    @Operation(
        summary = "Get Current User Profile",
        description = "Retrieves the current authenticated user's profile information"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<UserResponse> getCurrentUserProfile(@Parameter(hidden = true) Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UserResponse user = userService.getUser(principal.getUser().getId());
        if (user == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateUserProfile(Authentication authentication, @RequestBody UserUpdate updatedUser) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            UserResponse user = userService.updateUser(principal.getUser().getId(), updatedUser);
            if (user == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUser(id);
        if (user == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("#id == principal.id")
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
    @PreAuthorize("#id == principal.id")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("User deleted");
    }

    @GetMapping("/{id}/applications")
    @PreAuthorize("hasRole('FREELANCER') and #id == principal.id")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByUser(@PathVariable Long id) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByUser(id);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}/jobs")
    public ResponseEntity<List<JobResponse>> getJobsByUser(@PathVariable Long id, Authentication authentication) {
        System.out.println("UserController - getJobsByUser called for user ID: " + id);
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            System.out.println("UserController - Current user ID: " + principal.getUser().getId());
            System.out.println("UserController - Current user type: " + principal.getUser().getUserType());
            System.out.println("UserController - Current user authorities: " + principal.getAuthorities());
            
            // Manual authorization check
            if (!principal.getUser().getUserType().equals(UserType.employer)) {
                System.out.println("UserController - Access denied: User is not an employer");
                return ResponseEntity.status(403).build();
            }
            
            if (!principal.getUser().getId().equals(id)) {
                System.out.println("UserController - Access denied: User ID mismatch");
                return ResponseEntity.status(403).build();
            }
            System.out.println("UserController - Current user type: " + principal.getUser().getUserType());
            System.out.println("UserController - Current user authorities: " + principal.getAuthorities());
        }
        return ResponseEntity.ok(jobService.getJobsByCreator(id));
    }

    // User Skills endpoints
    @GetMapping("/skills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SkillResponse>> getUserSkills(Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            System.out.println("Getting skills for user: " + principal.getUser().getId());
            List<SkillResponse> skills = skillService.getUserSkills(principal.getUser().getId());
            System.out.println("Found " + skills.size() + " skills for user");
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            System.out.println("Error getting user skills: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/skills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SkillResponse> addUserSkill(Authentication authentication, @RequestBody AddUserSkillRequest request) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        SkillResponse skill = skillService.addUserSkill(principal.getUser().getId(), request.getSkillId(), request.getProficiencyLevel());
        return ResponseEntity.ok(skill);
    }

    @PutMapping("/skills/{skillId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SkillResponse> updateUserSkill(Authentication authentication, @PathVariable Long skillId, @RequestBody UpdateUserSkillRequest request) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        SkillResponse skill = skillService.updateUserSkill(principal.getUser().getId(), skillId, request.getProficiencyLevel());
        return ResponseEntity.ok(skill);
    }

    @DeleteMapping("/skills/{skillId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeUserSkill(Authentication authentication, @PathVariable Long skillId) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        skillService.removeUserSkill(principal.getUser().getId(), skillId);
        return ResponseEntity.ok().build();
    }
    
    // Get skills for a specific user (for employers viewing freelancer profiles)
    @GetMapping("/{id}/skills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SkillResponse>> getUserSkillsById(@PathVariable Long id) {
        try {
            System.out.println("Getting skills for user ID: " + id);
            List<SkillResponse> skills = skillService.getUserSkills(id);
            System.out.println("Found " + skills.size() + " skills for user ID: " + id);
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            System.out.println("Error getting user skills for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    
    // Test endpoint to check if skills service works
    @GetMapping("/test-skills")
    public ResponseEntity<List<SkillResponse>> testSkills() {
        try {
            System.out.println("Testing skills service...");
            List<SkillResponse> skills = skillService.getSkills();
            System.out.println("Found " + skills.size() + " available skills");
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            System.out.println("Error testing skills: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }



}
