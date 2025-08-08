package com.it355pz2.services;

import com.it355pz2.dto.JobDto;
import com.it355pz2.dto.JobResponse;
import com.it355pz2.dto.JobUpdate;

import java.util.List;

public interface JobService {

    List<JobResponse> getJobs();

    JobResponse getJob(Long id);

    List<JobResponse> getJobsByCreator(Long creatorId);

    JobResponse createJob(Long creatorId, JobDto dto);

    JobResponse updateJob(Long id, JobUpdate dto);

    boolean deleteJob(Long id);
}