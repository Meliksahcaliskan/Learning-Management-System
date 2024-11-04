package com.lsm.model.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.lsm.model.DTOs.LoginRequestDTO;
import com.lsm.repository.AppUserRepository;

@Component
public class LoginRequestValidator implements Validator {

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        /*
        LoginRequestDTO loginRequestDTO = (LoginRequestDTO) target;

        // Check if the username exists in the database
        AppUser appUser = userRepository.findByUsername(loginRequestDTO.getUsername());
        if (appUser == null) {
            errors.rejectValue("username", "error.username.not.found", "Username not found");
        } else {
            // Check if the password is correct
            if (!appUser.checkPassword(loginRequestDTO.getPassword())) {
                errors.rejectValue("password", "error.password.incorrect", "Incorrect password");
            }
        }
        */
    }

    // TODO: check for strong password
}