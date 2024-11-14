package com.lsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lsm.model.DTOs.LoginRequestDTO;
import com.lsm.model.DTOs.RegisterRequestDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AppUserRepository;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public AppUser registerUser(RegisterRequestDTO authRequest) {
        // Check if username or email already exists
        if (appUserRepository.findByUsername(authRequest.getUsername()) != null) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (appUserRepository.findByEmail(authRequest.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create new AppUser
        AppUser newUser = new AppUser();
        newUser.setUsername(authRequest.getUsername());
        newUser.setPassword(authRequest.getPassword()); // passwordEncoder.encode(authRequest.getPassword())
        newUser.setEmail(authRequest.getEmail());
        newUser.setRole(authRequest.getRole());

        // Save the user
        return appUserRepository.save(newUser);
    }

    public AppUser authenticate(LoginRequestDTO input) {
        try {
            // Attempt to authenticate the user
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    input.getUsername(),
                    input.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            // Handle bad credentials
            throw new IllegalArgumentException("Bad credentials");
        }

        // Check if the user exists after successful authentication
        AppUser user = appUserRepository.findByUsername(input.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("Username not found.");
        }

        return user;
    }

    //public AppUser findUserByUsername(String username) {
    //    return appUserRepository.findByUsername(username);
    //}
}
