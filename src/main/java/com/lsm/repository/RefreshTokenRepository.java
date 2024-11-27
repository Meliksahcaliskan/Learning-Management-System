package com.lsm.repository;

import com.lsm.model.entity.RefreshToken;
import com.lsm.model.entity.base.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(AppUser user);
    void deleteByToken(String token);
    void deleteByUserAndExpiryDateBefore(AppUser user, Instant expiryDate);
    long countByUser(AppUser user);
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user AND rt.id IN (SELECT r.id FROM RefreshToken r WHERE r.user = :user ORDER BY r.expiryDate ASC LIMIT 1)")
    @Modifying
    void deleteOldestByUser(@Param("user") AppUser user);
    List<RefreshToken> findByUserOrderByExpiryDateDesc(AppUser user);
}