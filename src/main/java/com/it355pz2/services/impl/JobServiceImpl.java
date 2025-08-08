package com.it355pz2.services.impl;

import com.it355pz2.dto.JobDto;
import com.it355pz2.dto.JobResponse;
import com.it355pz2.dto.JobUpdate;
import com.it355pz2.entity.Job;
import com.it355pz2.repository.JobRepository;
import com.it355pz2.repository.PaymentTypeRepository;
import com.it355pz2.repository.UserRepository;
import com.it355pz2.services.JobService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service
public class JobServiceImpl implements JobService {

    private JobRepository jobRepository;
    private UserRepository userRepository;
    private PaymentTypeRepository paymentTypeRepository;

    @Override
    public List<JobResponse> getJobs() {
        return jobRepository.findAllByIsDeletedFalse().stream().map(JobResponse::new).toList();
    }

    @Override
    public JobResponse getJob(Long id) {
        var job = jobRepository.findById(id).orElse(null);
        if (job == null || job.isDeleted()) return null;
        return new JobResponse(job);
    }

    @Override
    public List<JobResponse> getJobsByCreator(Long creatorId) {
        return jobRepository.findAllByCreateByUserIdAndIsDeletedFalse(creatorId).stream().map(JobResponse::new).toList();
    }

    @Override
    public JobResponse createJob(Long creatorId, JobDto dto) {
        var user = userRepository.findById(creatorId).orElse(null);
        if (user == null || user.isDeleted()) return null;

        var paymentType = paymentTypeRepository.findById(dto.getPaymentTypeId()).orElse(null);
        if (paymentType == null || paymentType.isDeleted()) return null;

        var job = new Job();

        job.setCreateByUser(user);
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setDateFrom(dto.getDateFrom());
        job.setDateTo(dto.getDateTo());
        job.setStatusType(dto.getStatus());
        job.setType(dto.getType());
        job.setLocation(dto.getLocation());
        job.setPaymentAmount(dto.getPaymentAmount());
        job.setPaymentType(paymentType);
        job.setUrgent(dto.getUrgent());
        job.setArchived(false);
        job.setDeleted(false);
        job.setCreatedAt(new Date().toString());
        job.setUpdatedAt(new Date().toString());

        jobRepository.save(job);
        return new JobResponse(job);
    }

    @Override
    public JobResponse updateJob(Long id, JobUpdate dto) {
        var job = jobRepository.findById(id).orElse(null);
        if (job == null || job.isDeleted()) return null;

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setDateFrom(dto.getDateFrom());
        job.setDateTo(dto.getDateTo());
        job.setStatusType(dto.getStatus());
        job.setType(dto.getType());
        job.setLocation(dto.getLocation());
        job.setPaymentAmount(dto.getPaymentAmount());
        job.setUrgent(dto.getUrgent());
        job.setUpdatedAt(new Date().toString());

        jobRepository.save(job);

        return new JobResponse(job);
    }

    @Override
    public boolean deleteJob(Long id) {
        var job = jobRepository.findById(id).orElse(null);
        if (job == null || job.isDeleted()) return false;

        job.setDeleted(true);
        jobRepository.save(job);
        return true;
    }
}
