package com.it355pz2.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.it355pz2.entity.enums.ApplicationStatus;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @JsonDeserialize(using = ApplicationStatusDeserializer.class)
    private ApplicationStatus status;
}


