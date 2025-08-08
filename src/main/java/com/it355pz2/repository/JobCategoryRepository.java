package com.it355pz2.repository;

import com.it355pz2.entity.JobCategory;
import com.it355pz2.entity.JobCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, JobCategoryId> {
}
