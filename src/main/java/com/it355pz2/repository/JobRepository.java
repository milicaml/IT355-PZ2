package com.it355pz2.repository;

import com.it355pz2.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.it355pz2.entity.enums.JobType;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findAllByIsDeletedFalse();

    Page<Job> findAllByIsDeletedFalse(Pageable pageable);

    List<Job> findAllByCreateByUserIdAndIsDeletedFalse(Long userId);

    Page<Job> findAllByIsDeletedFalseAndLocationContainingIgnoreCase(Pageable pageable, String location);

    Page<Job> findAllByIsDeletedFalseAndType(Pageable pageable, JobType type);

    @Query("SELECT j FROM Job j WHERE j.isDeleted = false AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> findAllByIsDeletedFalseAndTitleOrDescriptionContainingIgnoreCase(Pageable pageable, @Param("search") String search);

    @Query("SELECT j FROM Job j WHERE j.isDeleted = false AND LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')) AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> findAllByIsDeletedFalseAndLocationAndTitleOrDescriptionContainingIgnoreCase(Pageable pageable, @Param("location") String location, @Param("search") String search);

    Page<Job> findAllByIsDeletedFalseAndLocationContainingIgnoreCaseAndType(Pageable pageable, String location, JobType type);

    @Query("SELECT j FROM Job j WHERE j.isDeleted = false AND j.type = :type AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> findAllByIsDeletedFalseAndTypeAndTitleOrDescriptionContainingIgnoreCase(Pageable pageable, @Param("type") JobType type, @Param("search") String search);

    @Query("SELECT j FROM Job j WHERE j.isDeleted = false AND LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')) AND j.type = :type AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> findAllByIsDeletedFalseAndLocationAndTypeAndTitleOrDescriptionContainingIgnoreCase(Pageable pageable, @Param("location") String location, @Param("type") JobType type, @Param("search") String search);
}
