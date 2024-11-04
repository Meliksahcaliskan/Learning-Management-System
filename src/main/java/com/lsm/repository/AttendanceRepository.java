package com.lsm.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsm.model.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
