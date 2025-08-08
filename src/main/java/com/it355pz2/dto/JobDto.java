package com.it355pz2.dto;

import com.it355pz2.entity.enums.JobStatusType;
import com.it355pz2.entity.enums.JobType;
import lombok.Data;

@Data
public class JobDto {
    private String title;
    private String description;
    private String dateFrom;
    private String dateTo;
    private JobStatusType status;
    private JobType type;
    private String location;
    private double paymentAmount;
    private Long paymentTypeId;
    private int urgent;
}