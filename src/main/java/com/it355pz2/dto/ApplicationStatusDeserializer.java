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
            for (ApplicationStatus status : ApplicationStatus.values()) {
                if (status.getValue().equals(value)) {
                    return status;
                }
            }

            return ApplicationStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid application status: " + value);
        }
    }
}
