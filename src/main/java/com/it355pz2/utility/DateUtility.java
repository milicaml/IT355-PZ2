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
            if (dateString.contains("CEST") || dateString.contains("CET")) {
                return LocalDateTime.now().format(ISO_FORMATTER);
            }

            if (dateString.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
                return dateString;
            }

            return LocalDateTime.now().format(ISO_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now().format(ISO_FORMATTER);
        }
    }
}
