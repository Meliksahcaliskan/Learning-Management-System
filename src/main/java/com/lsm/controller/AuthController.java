package com.lsm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lsm.model.DTOs.LoginRequestDTO;
import com.lsm.model.DTOs.LoginResponseDTO;
import com.lsm.model.DTOs.RegisterRequestDTO;
import com.lsm.model.DTOs.RegisterResponseDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.service.AuthService;
import com.lsm.service.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    // private final AppUserService appUserService;
    private final AuthService authService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          /*AppUserService appUserService,*/ AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        // this.appUserService = appUserService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO authRequest) {
        try {
            // Authenticate user
            AppUser authenticatedUser = authService.authenticate(authRequest);
            
            // Generate JWT token
            String jwtToken = jwtTokenProvider.generateToken(authenticatedUser);
            
            // Create login response DTO
            LoginResponseDTO loginResponse = new LoginResponseDTO(authenticatedUser, jwtToken);
            
            // Return the successful login response
            return ResponseEntity.ok(loginResponse);
            
        } catch (IllegalArgumentException e) {
            // Return 401 Unauthorized when credentials are invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }



    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO authRequest) {
        try {
            AppUser newUser = authService.registerUser(authRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new RegisterResponseDTO(newUser.getId(), "Registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponseDTO(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RegisterResponseDTO(null, "An unexpected error occurred"));
        }
    }

}