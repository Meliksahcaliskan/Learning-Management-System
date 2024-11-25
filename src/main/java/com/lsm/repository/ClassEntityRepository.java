package com.lsm.repository;


import com.lsm.model.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {
    @Query("SELECT c FROM ClassEntity c " +
            "LEFT JOIN FETCH c.assignments " +
            "LEFT JOIN FETCH c.students " +
            "LEFT JOIN FETCH c.courses")
    List<ClassEntity> findAllWithAssociations();
}
