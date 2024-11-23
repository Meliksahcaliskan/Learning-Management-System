package com.lsm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // Find assignments by teacher
    List<Assignment> findByAssignedBy(AppUser teacher);

    // Find assignments by class
    List<Assignment> findByClassId(Long classId);

    // Find assignments by course
    List<Assignment> findByCourseId(Long courseId);

    // Find assignments by class and teacher
    List<Assignment> findByClassIdAndAssignedBy(Long classId, AppUser teacher);

    // Find assignments by course and teacher
    List<Assignment> findByCourseIdAndAssignedBy(Long courseId, AppUser teacher);

    // Find assignments by due date for a class
    List<Assignment> findByClassIdOrderByDueDateDesc(Long classId);

    // Find assignments by due date for a course
    List<Assignment> findByCourseIdOrderByDueDateDesc(Long courseId);

    // Check if assignment exists by title for a class
    boolean existsByTitleAndClassId(String title, Long classId);
}