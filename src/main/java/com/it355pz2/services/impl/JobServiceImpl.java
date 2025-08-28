package com.it355pz2.services.impl;

import com.it355pz2.dto.JobDto;
import com.it355pz2.dto.JobResponse;
import com.it355pz2.dto.JobUpdate;
import com.it355pz2.entity.Job;
import com.it355pz2.entity.JobCategory;
import com.it355pz2.entity.Category;

import com.it355pz2.entity.Skill;
import com.it355pz2.repository.JobRepository;
import com.it355pz2.repository.PaymentTypeRepository;
import com.it355pz2.repository.UserRepository;
import com.it355pz2.repository.CategoryRepository;
import com.it355pz2.repository.JobCategoryRepository;

import com.it355pz2.repository.SkillRepository;
import com.it355pz2.services.JobService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.it355pz2.utility.DateUtility.getCurrentDateTime;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import com.it355pz2.entity.enums.JobType;

@AllArgsConstructor
@Service
public class JobServiceImpl implements JobService {

    private JobRepository jobRepository;
    private UserRepository userRepository;
    private PaymentTypeRepository paymentTypeRepository;
    private CategoryRepository categoryRepository;
    private JobCategoryRepository jobCategoryRepository;
    private SkillRepository skillRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getJobs() {
        List<Job> jobs = jobRepository.findAllByIsDeletedFalse();
        return jobs.stream().map(job -> {
            List<JobCategory> jobCategories = jobCategoryRepository.findByJobId(job.getId());
            List<String> categoryNames = new ArrayList<>();
            for (JobCategory jobCategory : jobCategories) {
                Category category = categoryRepository.findById(jobCategory.getCategoryId()).orElse(null);
                if (category != null) {
                    categoryNames.add(category.getTitle());
                }
            }
            JobResponse response = new JobResponse(job);
            response.setCategories(categoryNames);
            return response;
        }).toList();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getJobsPaginated(Pageable pageable, String location, String search, String type) {
        Page<Job> jobsPage;

        JobType jobType = null;
        if (type != null && !type.trim().isEmpty()) {
            try {
                jobType = JobType.valueOf(type.trim());
            } catch (IllegalArgumentException e) {
                jobsPage = jobRepository.findAllByIsDeletedFalse(pageable);
                return jobsPage.map(job -> {
                    List<JobCategory> jobCategories = jobCategoryRepository.findByJobId(job.getId());
                    List<String> categoryNames = new ArrayList<>();
                    for (JobCategory jobCategory : jobCategories) {
                        categoryRepository.findById(jobCategory.getCategoryId()).ifPresent(category -> categoryNames.add(category.getTitle()));
                    }
                    JobResponse response = new JobResponse(job);
                    response.setCategories(categoryNames);
                    return response;
                });
            }
        }

        if (location != null && !location.trim().isEmpty() &&
                search != null && !search.trim().isEmpty() &&
                jobType != null) {
            jobsPage = jobRepository.findAllByIsDeletedFalseAndLocationAndTypeAndTitleOrDescriptionContainingIgnoreCase(
                    pageable, location.trim(), jobType, search.trim());
        } else if (location != null && !location.trim().isEmpty() &&
                search != null && !search.trim().isEmpty()) {
            jobsPage = jobRepository.findAllByIsDeletedFalseAndLocationAndTitleOrDescriptionContainingIgnoreCase(
                    pageable, location.trim(), search.trim());
        } else if (location != null && !location.trim().isEmpty() &&
                jobType != null) {
            jobsPage = jobRepository.findAllByIsDeletedFalseAndLocationContainingIgnoreCaseAndType(
                    pageable, location.trim(), jobType);
        } else if (search != null && !search.trim().isEmpty() &&
                jobType != null) {
            jobsPage = jobRepository.findAllByIsDeletedFalseAndTypeAndTitleOrDescriptionContainingIgnoreCase(
                    pageable, jobType, search.trim());
        } else if (location != null && !location.trim().isEmpty()) {
            jobsPage = jobRepository.findAllByIsDeletedFalseAndLocationContainingIgnoreCase(
                    pageable, location.trim());
        } else if (search != null && !search.trim().isEmpty()) {
            jobsPage = jobRepository.findAllByIsDeletedFalseAndTitleOrDescriptionContainingIgnoreCase(
                    pageable, search.trim());
        } else if (jobType != null) {
            jobsPage = jobRepository.findAllByIsDeletedFalseAndType(
                    pageable, jobType);
        } else {
            jobsPage = jobRepository.findAllByIsDeletedFalse(pageable);
        }

        return jobsPage.map(job -> {
            List<JobCategory> jobCategories = jobCategoryRepository.findByJobId(job.getId());
            List<String> categoryNames = new ArrayList<>();
            for (JobCategory jobCategory : jobCategories) {
                Category category = categoryRepository.findById(jobCategory.getCategoryId()).orElse(null);
                if (category != null) {
                    categoryNames.add(category.getTitle());
                }
            }
            JobResponse response = new JobResponse(job);
            response.setCategories(categoryNames);
            return response;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse getJob(Long id) {
        var job = jobRepository.findById(id).orElse(null);
        if (job == null || job.isDeleted()) return null;

        List<JobCategory> jobCategories = jobCategoryRepository.findByJobId(job.getId());
        List<String> categoryNames = new ArrayList<>();
        for (JobCategory jobCategory : jobCategories) {
            Category category = categoryRepository.findById(jobCategory.getCategoryId()).orElse(null);
            if (category != null) {
                categoryNames.add(category.getTitle());
            }
        }
        JobResponse response = new JobResponse(job);
        response.setCategories(categoryNames);
        return response;
    }

    @Override
    public List<JobResponse> getJobsByCreator(Long creatorId) {
        try {
            List<Job> jobs = jobRepository.findAllByCreateByUserIdAndIsDeletedFalse(creatorId);

            return jobs.stream().map(job -> {
                List<JobCategory> jobCategories = jobCategoryRepository.findByJobId(job.getId());
                List<String> categoryNames = new ArrayList<>();
                for (JobCategory jobCategory : jobCategories) {
                    Category category = categoryRepository.findById(jobCategory.getCategoryId()).orElse(null);
                    if (category != null) {
                        categoryNames.add(category.getTitle());
                    }
                }
                JobResponse response = new JobResponse(job);
                response.setCategories(categoryNames);
                return response;
            }).toList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
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
        job.setCreatedAt(getCurrentDateTime());
        job.setUpdatedAt(new Date().toString());

        jobRepository.save(job);

        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            for (Number categoryId : dto.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId.longValue()).orElse(null);
                if (category != null && !category.isDeleted()) {
                    JobCategory jobCategory = new JobCategory();
                    jobCategory.setJobId(job.getId());
                    jobCategory.setCategoryId(categoryId.longValue());
                    jobCategoryRepository.save(jobCategory);
                }
            }
        }

        List<JobCategory> jobCategories = jobCategoryRepository.findByJobId(job.getId());
        List<String> categoryNames = new ArrayList<>();
        for (JobCategory jobCategory : jobCategories) {
            Category category = categoryRepository.findById(jobCategory.getCategoryId()).orElse(null);
            if (category != null) {
                categoryNames.add(category.getTitle());
            }
        }
        JobResponse response = new JobResponse(job);
        response.setCategories(categoryNames);
        return response;
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
        job.setUpdatedAt(getCurrentDateTime());

        jobRepository.save(job);

        if (dto.getCategoryIds() != null) {
            List<JobCategory> existingJobCategories = jobCategoryRepository.findByJobId(job.getId());
            jobCategoryRepository.deleteAll(existingJobCategories);

            for (Number categoryId : dto.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId.longValue()).orElse(null);
                if (category != null && !category.isDeleted()) {
                    JobCategory jobCategory = new JobCategory();
                    jobCategory.setJobId(job.getId());
                    jobCategory.setCategoryId(categoryId.longValue());
                    jobCategoryRepository.save(jobCategory);
                }
            }
        }

        List<JobCategory> jobCategories = jobCategoryRepository.findByJobId(job.getId());
        List<String> categoryNames = new ArrayList<>();
        for (JobCategory jobCategory : jobCategories) {
            Category category = categoryRepository.findById(jobCategory.getCategoryId()).orElse(null);
            if (category != null) {
                categoryNames.add(category.getTitle());
            }
        }
        JobResponse response = new JobResponse(job);
        response.setCategories(categoryNames);
        return response;
    }

    @Override
    public boolean deleteJob(Long id) {
        var job = jobRepository.findById(id).orElse(null);
        if (job == null || job.isDeleted()) return false;

        job.setDeleted(true);
        jobRepository.save(job);
        return true;
    }

    @Override
    public boolean isJobOwner(Long jobId, Long userId) {
        var job = jobRepository.findById(jobId).orElse(null);
        return job != null && job.getCreateByUser().getId().equals(userId);
    }
}
