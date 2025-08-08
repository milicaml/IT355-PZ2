package com.it355pz2.controllers;

import com.it355pz2.dto.JWTAuthResponse;
import com.it355pz2.dto.LoginDto;
import com.it355pz2.dto.RegisterDto;
import com.it355pz2.security.JwtTokenProvider;
import com.it355pz2.services.AuthService;
import com.it355pz2.services.impl.AuthServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> authenticate(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<JWTAuthResponse> register(@RequestBody RegisterDto registerDto) {
        authService.register(registerDto);

        String token = authService.login(new LoginDto(registerDto.getUsername(), registerDto.getPassword()));

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String authHeader) {
        boolean status = authService.validate(authHeader);
        return ResponseEntity.ok(status);
    }
}