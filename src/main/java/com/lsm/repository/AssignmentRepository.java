package com.lsm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lsm.model.entity.Assignment;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Override
    Optional<Assignment> findById(Long id);
}
