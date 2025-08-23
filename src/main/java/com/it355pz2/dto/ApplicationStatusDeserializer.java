package com.it355pz2.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.it355pz2.entity.enums.ApplicationStatus;

import java.io.IOException;

public class ApplicationStatusDeserializer extends JsonDeserializer<ApplicationStatus> {
    @Override
    public ApplicationStatus deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null) {
            return null;
        }
        
        try {
            // First try to find by value directly (for lowercase values like "pending")
            for (ApplicationStatus status : ApplicationStatus.values()) {
                if (status.getValue().equals(value)) {
                    return status;
                }
            }
            
            // If not found by value, try to convert to enum name
            return ApplicationStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid application status: " + value);
        }
    }
}
