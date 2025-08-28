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

        if (isPublicEndpoint(requestURI, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {

            if (jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsername(token);

                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    var authorities = jwtTokenProvider.getAuthorities(token);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI, String method) {

        if ("OPTIONS".equals(method)) {
            return true;
        }

        if ("GET".equals(method)) {

            return requestURI.startsWith("/api/auth/") ||
                    requestURI.startsWith("/api/categories/") ||
                    requestURI.startsWith("/api/skills/") ||
                    requestURI.startsWith("/api/payment-types/") ||
                    requestURI.equals("/api/jobs") ||
                    requestURI.startsWith("/api/jobs/");
        }
        return false;
    }


}
