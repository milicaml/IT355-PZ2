package com.it355pz2.services;

import com.it355pz2.dto.JobDto;
import com.it355pz2.dto.JobResponse;
import com.it355pz2.dto.JobUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {

    List<JobResponse> getJobs();

    Page<JobResponse> getJobsPaginated(Pageable pageable, String location, String search, String type);

    JobResponse getJob(Long id);

    List<JobResponse> getJobsByCreator(Long creatorId);

    JobResponse createJob(Long creatorId, JobDto dto);

    JobResponse updateJob(Long id, JobUpdate dto);

    boolean deleteJob(Long id);
    
    boolean isJobOwner(Long jobId, Long userId);
}