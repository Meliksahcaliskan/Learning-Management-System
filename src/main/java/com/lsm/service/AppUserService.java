package com.lsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AppUserRepository;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return appUser;
    }

    public AppUser findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }
}