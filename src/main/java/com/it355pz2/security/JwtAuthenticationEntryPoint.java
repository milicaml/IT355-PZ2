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

        if (isProtectedEndpoint(requestURI, method)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private boolean isProtectedEndpoint(String requestURI, String method) {
        if ("GET".equals(method) && requestURI.startsWith("/api/jobs")) {
            return false;
        }

        else if (requestURI.startsWith("/api/auth/")) {
            return false;
        }

        else if ("GET".equals(method) && (
                requestURI.startsWith("/api/categories/") ||
                        requestURI.startsWith("/api/skills/") ||
                        requestURI.startsWith("/api/payment-types/")
        )) {
            return false;
        }

        return true;
    }
}
