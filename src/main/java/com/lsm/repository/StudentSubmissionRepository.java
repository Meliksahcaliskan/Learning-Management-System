package com.lsm.repository;

import com.lsm.model.entity.StudentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentSubmissionRepository extends JpaRepository<StudentSubmission, Long> {
    Optional<StudentSubmission> findByAssignment_IdAndStudent_Id(Long assignmentId, Long studentId);
}
