package com.it355pz2.services;

import com.it355pz2.dto.ApplicationResponse;
import com.it355pz2.entity.Application;
import com.it355pz2.entity.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse createApplication(Long userId,Long jobId,String description);
    ApplicationResponse getApplication(Long id);
    List<ApplicationResponse> getApplicationsByUser(Long userId);
    List<ApplicationResponse> getApplicationsForEmployer(Long employerId);
    List<ApplicationResponse> getApplicationsByJob(Long jobId);
    boolean deleteApplication(Long id);
    List<ApplicationResponse> getApplicationsByStatus(Long jobId,ApplicationStatus status);
    ApplicationResponse updateStatus(Long id,ApplicationStatus status);
    boolean hasUserAppliedForJob(Long userId, Long jobId);
}
