package com.it355pz2.services.impl;

import com.it355pz2.dto.ApplicationResponse;
import com.it355pz2.entity.Application;
import com.it355pz2.entity.Job;
import com.it355pz2.entity.User;
import com.it355pz2.entity.enums.ApplicationStatus;
import com.it355pz2.repository.ApplicationRepository;
import com.it355pz2.repository.JobRepository;
import com.it355pz2.repository.UserRepository;
import com.it355pz2.services.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import static com.it355pz2.utility.DateUtility.getCurrentDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class ApplicationServiceImpl implements ApplicationService {
    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;
    private JobRepository jobRepository;
    @Override
    public ApplicationResponse createApplication(Long userId, Long jobId, String description) {
        if (userId == null || jobId == null || description == null) return null;
        
        // Check if user has already applied for this job
        if (applicationRepository.existsByUserIdAndJobIdAndIsDeletedFalse(userId, jobId)) {
            return null;
        }
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null) return null;
        
        Application application = new Application();
        application.setUser(user);
        application.setJob(job);
        application.setDescription(description);
        application.setStatus(ApplicationStatus.pending);
        application.setCreatedAt(getCurrentDateTime());
        application.setUpdatedAt(new Date().toString());

        return new ApplicationResponse(applicationRepository.save(application));
    }

    @Override
    public ApplicationResponse getApplication(Long id) {
        Application application = applicationRepository.findById(id).orElse(null);
        if (application == null) return null;
        return new ApplicationResponse(application);
    }

    @Override
    public List<ApplicationResponse> getApplicationsByUser(Long userId) {
        List<Application> applications = applicationRepository.findAllByUserId(userId);
        return applications.stream().map(ApplicationResponse::new).toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsForEmployer(Long employerId) {
        List<Job> employerJobs = jobRepository.findAllByCreateByUserIdAndIsDeletedFalse(employerId);
        List<Application> allApplications = applicationRepository.findAll();
        
        List<Application> employerApplications = allApplications.stream()
            .filter(application -> employerJobs.stream()
                .anyMatch(job -> job.getId().equals(application.getJob().getId())))
            .toList();
        
        return employerApplications.stream().map(ApplicationResponse::new).toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJob(Long jobId) {
        List<Application> applications = applicationRepository.findAllByJobId(jobId);
        return applications.stream().map(ApplicationResponse::new).toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsByStatus(Long jobId, ApplicationStatus status) {
        List<Application> applications = applicationRepository.findAllByJobIdAndStatus(jobId,status);
        return applications.stream().map(ApplicationResponse::new).toList();
    }

    @Override
    public boolean deleteApplication(Long id) {
        Application application = applicationRepository.findById(id).orElse(null);
        if (application == null || application.isDeleted()) return false;
        application.setDeleted(true);
        application.setUpdatedAt(new Date().toString());

        applicationRepository.save(application);
        return true;
    }

    @Override
    public ApplicationResponse updateStatus(Long id, ApplicationStatus status) {
        Application application = applicationRepository.findById(id).orElse(null);
        if(application == null || application.getStatus().equals(status)) return null;
        application.setStatus(status);
        applicationRepository.save(application);
        return new ApplicationResponse(application);
    }

    @Override
    public boolean hasUserAppliedForJob(Long userId, Long jobId) {
        return applicationRepository.existsByUserIdAndJobIdAndIsDeletedFalse(userId, jobId);
    }
}
