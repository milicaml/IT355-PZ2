package com.it355pz2.filter;


import com.it355pz2.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static com.it355pz2.utility.TokenUtility.getTokenFromRequest;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Skip JWT processing for public endpoints
        if (isPublicEndpoint(requestURI, method)) {
            System.out.println("JWT Filter - Skipping public endpoint: " + requestURI);
            // For public endpoints, just continue the filter chain without setting authentication
            filterChain.doFilter(request, response);
            return;
        }
        
        String token = getTokenFromRequest(request);
        System.out.println("JWT Filter - Request URI: " + requestURI);
        System.out.println("JWT Filter - Method: " + method);
        System.out.println("JWT Filter - Token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));

        if (StringUtils.hasText(token)) {
            System.out.println("JWT Filter - Token validation result: " + jwtTokenProvider.validateToken(token));
            
            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);
                System.out.println("JWT Filter - Username: " + username);

                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    var authorities = jwtTokenProvider.getAuthorities(token);
                    System.out.println("JWT Filter - Authorities from token: " + authorities);
                    System.out.println("JWT Filter - UserDetails authorities: " + userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("JWT Filter - Authentication set successfully for: " + requestURI);
                    System.out.println("JWT Filter - Final authentication: " + SecurityContextHolder.getContext().getAuthentication());
                } catch (Exception e) {
                    System.out.println("JWT Filter - Error loading user details: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("JWT Filter - Token validation failed for: " + requestURI);
            }
        } else {
            System.out.println("JWT Filter - No token found for: " + requestURI);
            System.out.println("JWT Filter - Authorization header: " + request.getHeader("Authorization"));
        }

        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicEndpoint(String requestURI, String method) {
        System.out.println("JWT Filter - Checking if public endpoint: " + requestURI + " with method: " + method);
        
        // Allow OPTIONS requests (CORS preflight)
        if ("OPTIONS".equals(method)) {
            return true;
        }
        
        if ("GET".equals(method)) {
            boolean isPublic = requestURI.startsWith("/api/auth/") ||
                   requestURI.startsWith("/api/categories/") ||
                   requestURI.startsWith("/api/skills/") ||
                   requestURI.startsWith("/api/payment-types/") ||
                   requestURI.equals("/api/jobs") ||
                   requestURI.startsWith("/api/jobs/");
            
            System.out.println("JWT Filter - Is public endpoint: " + isPublic);
            return isPublic;
        }
        return false;
    }
    

}
