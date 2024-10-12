package com.lms.lms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lms.lms.modules.AppUserManagement.AppUser;
import com.lms.lms.modules.AppUserManagement.AppUserRepository;

@RestController
public class RegistrationController {
    
    @Autowired
    private AppUserRepository appUserRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping(value = "/req/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@RequestBody AppUser user) {
        // Perform server-side validation
        if (user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("All fields are required");
        }

        // Check for password match
        //if (!user.getPassword().equals(user.getConfirmPassword())) {
        //    return ResponseEntity.badRequest().body("Passwords do not match");
        //}

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user if all validations pass
        appUserRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
    
}