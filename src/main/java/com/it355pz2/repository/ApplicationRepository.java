package com.it355pz2.repository;

import com.it355pz2.entity.Application;
import com.it355pz2.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findAllByJobId(Long jobId);

    List<Application> findAllByUserId(Long userId);

    List<Application> findAllByJobIdAndStatus(Long jobId, ApplicationStatus status);
    
    boolean existsByUserIdAndJobIdAndIsDeletedFalse(Long userId, Long jobId);
}