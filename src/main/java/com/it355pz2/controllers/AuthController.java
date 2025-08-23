package com.it355pz2.controllers;

import com.it355pz2.dto.JWTAuthResponse;
import com.it355pz2.dto.LoginDto;
import com.it355pz2.dto.RegisterDto;
import com.it355pz2.security.JwtTokenProvider;
import com.it355pz2.services.AuthService;
import com.it355pz2.services.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.it355pz2.security.UserPrincipal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private AuthService authService;
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    @Operation(
        summary = "User Login",
        description = "Authenticates a user and returns a JWT token for API access"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JWTAuthResponse.class),
                examples = @ExampleObject(
                    name = "Successful Login",
                    value = """
                    {
                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "tokenType": "Bearer"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid Credentials",
                    value = """
                    {
                        "timestamp": "2024-01-15T10:30:00Z",
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Invalid username or password"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<JWTAuthResponse> authenticate(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/register")
    @Operation(
        summary = "User Registration",
        description = "Registers a new user account and returns a JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Registration successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JWTAuthResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid registration data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                    {
                        "timestamp": "2024-01-15T10:30:00Z",
                        "status": 400,
                        "error": "Bad Request",
                        "message": "Username already exists"
                    }
                    """
                )
            )
        )
    })
    public ResponseEntity<JWTAuthResponse> register(@RequestBody RegisterDto registerDto) {
        authService.register(registerDto);

        String token = authService.login(new LoginDto(registerDto.getUsername(), registerDto.getPassword()));

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @GetMapping("/validate")
    @Operation(
        summary = "Validate JWT Token",
        description = "Validates the current JWT token and returns true if valid"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token validation result",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Valid Token",
                    value = "true"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid or expired token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Invalid Token",
                    value = "false"
                )
            )
        )
    })
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String authHeader) {
        boolean status = authService.validate(authHeader);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/debug")
    public ResponseEntity<Map<String, Object>> debugAuth(Authentication authentication) {
        Map<String, Object> debugInfo = new HashMap<>();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            debugInfo.put("authenticated", true);
            debugInfo.put("username", principal.getUsername());
            debugInfo.put("userType", principal.getUser().getUserType());
            debugInfo.put("authorities", principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
            debugInfo.put("userId", principal.getUser().getId());
        } else {
            debugInfo.put("authenticated", false);
            debugInfo.put("principal", authentication != null ? authentication.getPrincipal().getClass().getSimpleName() : "null");
        }
        
        return ResponseEntity.ok(debugInfo);
    }

    @GetMapping("/token-info")
    public ResponseEntity<Map<String, Object>> getTokenInfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> tokenInfo = jwtTokenProvider.getTokenInfo(token);
        return ResponseEntity.ok(tokenInfo);
    }
}