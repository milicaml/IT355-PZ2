package com.it355pz2.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.it355pz2.entity.enums.JobStatusType;

import java.io.IOException;

public class JobStatusTypeDeserializer extends JsonDeserializer<JobStatusType> {
    @Override
    public JobStatusType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null) {
            return null;
        }

        try {
            for (JobStatusType statusType : JobStatusType.values()) {
                if (statusType.getValue().equals(value)) {
                    return statusType;
                }
            }

            return JobStatusType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid job status type: " + value);
        }
    }
}


