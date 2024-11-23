package com.lsm.controller;

import com.lsm.model.DTOs.auth.RegisterRequestDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.AppUserService;
import com.lsm.service.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Arrays;

@Controller
@RequestMapping("/api/v1/auth")
@Slf4j
public class ContentController {

    private final AppUserService userService;
    private final JwtTokenProvider tokenProvider;

    public ContentController(AppUserService userService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/api/v1/auth/index";
        }

        // Add any error messages from previous login attempts
        String error = (String) request.getSession().getAttribute("error");
        if (error != null) {
            model.addAttribute("error", error);
            request.getSession().removeAttribute("error");
        }

        // Add success message if redirected from registration
        String success = (String) request.getSession().getAttribute("success");
        if (success != null) {
            model.addAttribute("success", success);
            request.getSession().removeAttribute("success");
        }

        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/api/v1/auth/index";
        }

        // Add the registration form backing object
        model.addAttribute("registerRequest", new RegisterRequestDTO());
        model.addAttribute("roles", Arrays.asList(Role.values()));

        return "signup";
    }

    @GetMapping("/index")
    public String home(Model model, Principal principal) {
        // Ensure user is authenticated
        if (principal == null) {
            return "redirect:/api/v1/auth/login";
        }

        try {
            // Get user details
            AppUser user = userService.findByUsername(principal.getName());
            if(user == null)
                throw new UsernameNotFoundException("User not found");

            // Add user information to model
            model.addAttribute("username", user.getUsername());
            model.addAttribute("role", user.getRole());
            model.addAttribute("email", user.getEmail());

            // Add role-specific data
            if (user.getRole() == Role.ROLE_TEACHER) {
                model.addAttribute("isTeacher", true);
                // Add teacher-specific data
            } else if (user.getRole() == Role.ROLE_STUDENT) {
                model.addAttribute("isStudent", true);
                // Add student-specific data
            }

            return "index";
        } catch (Exception e) {
            log.error("Error loading home page for user: {}", principal.getName(), e);
            return "redirect:/api/v1/auth/login";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "error/access-denied";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/api/v1/auth/login";
        }

        try {
            AppUser user = userService.findByUsername(principal.getName());
            if(user == null)
                throw new UsernameNotFoundException("User not found");

            model.addAttribute("user", user);
            return "profile";
        } catch (Exception e) {
            log.error("Error loading profile for user: {}", principal.getName(), e);
            return "redirect:/api/v1/auth/login";
        }
    }
}
