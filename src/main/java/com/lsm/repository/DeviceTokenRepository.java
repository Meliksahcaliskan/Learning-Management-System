package com.lsm.repository;

import com.lsm.model.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    List<DeviceToken> findByUserId(Long userId);
    void deleteByDeviceToken(String deviceToken);
}
