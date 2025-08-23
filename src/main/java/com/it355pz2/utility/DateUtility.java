package com.it355pz2.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtility {
    
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(ISO_FORMATTER);
    }
    
    public static String formatDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Try to parse existing date formats and convert to ISO
            if (dateString.contains("CEST") || dateString.contains("CET")) {
                // Handle old Date.toString() format
                return LocalDateTime.now().format(ISO_FORMATTER);
            }
            
            // If it's already in ISO format, return as is
            if (dateString.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                return dateString;
            }
            
            // Default to current date if format is unknown
            return LocalDateTime.now().format(ISO_FORMATTER);
        } catch (Exception e) {
            // Return current date if parsing fails
            return LocalDateTime.now().format(ISO_FORMATTER);
        }
    }
}
