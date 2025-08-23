package com.it355pz2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.it355pz2.entity.enums.JobStatusType;
import com.it355pz2.entity.enums.JobType;
import lombok.Data;

import java.util.List;

@Data
public class JobDto {
    private String title;
    private String description;
    private String dateFrom;
    private String dateTo;
    
    @JsonDeserialize(using = JobStatusTypeDeserializer.class)
    private JobStatusType status;
    
    @JsonDeserialize(using = JobTypeDeserializer.class)
    private JobType type;
    
    private String location;
    private double paymentAmount;
    private Long paymentTypeId;
    private int urgent;
    private List<Number> categoryIds;
    private List<Number> skillIds;
}