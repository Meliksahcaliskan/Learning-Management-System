package com.lsm.repository;

import com.lsm.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    boolean existsByCode(String code);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.classes WHERE c.id = :id")
    Optional<Course> findByIdWithClasses(Long id);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.assignments")
    List<Course> findAllWithAssignments();

    @Query("SELECT c FROM Course c WHERE c.id IN (SELECT cc.id FROM ClassEntity cl JOIN cl.courses cc WHERE cl.id = :classId)")
    List<Course> findByClassId(Long classId);

    Optional<Course> findCourseByName(String courseName);
}