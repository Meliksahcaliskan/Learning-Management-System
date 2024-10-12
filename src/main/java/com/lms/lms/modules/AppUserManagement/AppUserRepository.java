package com.lms.lms.modules.AppUserManagement;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    
    Optional<AppUser> findByUsername(String username);

}
