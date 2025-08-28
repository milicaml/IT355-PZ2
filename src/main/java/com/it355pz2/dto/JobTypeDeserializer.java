package com.it355pz2.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.it355pz2.entity.enums.JobType;

import java.io.IOException;

public class JobTypeDeserializer extends JsonDeserializer<JobType> {
    @Override
    public JobType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null) {
            return null;
        }

        try {
            for (JobType jobType : JobType.values()) {
                if (jobType.getValue().equals(value)) {
                    return jobType;
                }
            }

            return JobType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid job type: " + value);
        }
    }
}


