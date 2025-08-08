package com.it355pz2.dto;

import com.it355pz2.entity.Application;
import com.it355pz2.entity.Job;
import com.it355pz2.entity.User;
import com.it355pz2.entity.enums.ApplicationStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ApplicationResponse {
    private Long userId;
    private String userFullName;
    private Long jobId;
    private String jobTitle;
    private String description;
    private ApplicationStatus status;

    public ApplicationResponse(Application application) {
        this.userId = application.getUser().getId();
        this.userFullName = application.getUser().getFullName();
        this.jobId = application.getJob().getId();
        this.jobTitle = application.getJob().getTitle();
        this.description = application.getDescription();
        this.status = application.getStatus();
    }

}
