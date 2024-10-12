package com.lms.lms.modules.AppUserManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppUserController {
    
    private final AppUserService userService;

    @Autowired
    public AppUserController(AppUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/v1/users/{username}")
    public UserDetails getUserByUsername(@PathVariable String username) {
        return userService.loadUserByUsername(username);
    }

}
