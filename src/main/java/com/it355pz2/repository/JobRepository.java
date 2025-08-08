package com.it355pz2.repository;

import com.it355pz2.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {


    List<Job> findAllByIsDeletedFalse();

    List<Job> findAllByCreateByUserIdAndIsDeletedFalse(Long userId);
}
