package com.it355pz2.utility;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class TokenUtility {
    public static String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return getTokenFromBearer(bearerToken);
    }

    public static String getTokenFromBearer(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}