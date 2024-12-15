package com.lsm.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsm.model.entity.Attendance;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentId(Long studentId);
    List<Attendance> findByStudentIdAndClassId(Long studentId, Long classId);
    List<Attendance> findByCourseId(Long courseId);
    List<Attendance> findByCourseIdAndClassId(Long courseId, Long classId);
    Object findAllByCourseId(Long courseId);
    List<Attendance> findByCourseIdAndClassIdAndDateBetween(
            Long courseId, Long classId, LocalDate date, LocalDate date2
    );
    List<Attendance> findByCourseIdAndDateBetween(
            Long courseId, LocalDate date, LocalDate date2
    );
}
