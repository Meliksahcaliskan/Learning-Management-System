package com.lsm.service;

import com.lsm.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AppUserRepository;

import java.util.Optional;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> userOpt = appUserRepository.findByUsername(username);
        return userOpt.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public AppUser findByUsername(String username) {
        Optional<AppUser> userOpt = appUserRepository.findByUsername(username);
        return userOpt.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }
}