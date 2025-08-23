package com.it355pz2.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Only handle authentication failures for protected endpoints
        if (isProtectedEndpoint(requestURI, method)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        } else {
            // For public endpoints, just continue without authentication
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
    
    private boolean isProtectedEndpoint(String requestURI, String method) {
        // Define protected endpoints that require authentication
        if ("POST".equals(method) && requestURI.startsWith("/api/jobs")) {
            return true;
        }
        if ("PUT".equals(method) && requestURI.startsWith("/api/jobs/")) {
            return true;
        }
        if ("DELETE".equals(method) && requestURI.startsWith("/api/jobs/")) {
            return true;
        }
        if (requestURI.startsWith("/api/users/profile")) {
            return true;
        }
        if (requestURI.startsWith("/api/users/") && !requestURI.equals("/api/users/")) {
            return true;
        }
        if (requestURI.startsWith("/api/applications")) {
            return true;
        }
        
        // GET requests to jobs are public
        if ("GET".equals(method) && requestURI.startsWith("/api/jobs")) {
            return false;
        }
        
        // Auth endpoints are public
        if (requestURI.startsWith("/api/auth/")) {
            return false;
        }
        
        // Categories, skills, payment-types are public for GET
        if ("GET".equals(method) && (
            requestURI.startsWith("/api/categories/") ||
            requestURI.startsWith("/api/skills/") ||
            requestURI.startsWith("/api/payment-types/")
        )) {
            return false;
        }
        
        // Default to protected for any other endpoints
        return true;
    }
}
