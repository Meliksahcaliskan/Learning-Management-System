package com.lsm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lsm.security.JwtAuthenticationFilter;
import com.lsm.service.AppUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserService appUserService;

    @Autowired
    public SecurityConfig(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return appUserService;
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // disable csrf for stateless API
                .authorizeHttpRequests(registry -> { // TODO: Register should be restricted later.
                    registry.requestMatchers("/api/auth/register", "/api/auth/login", "/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/css/**", "/js/**").permitAll();
                    registry.requestMatchers(HttpMethod.POST, "api/assignments/createAssignment").hasAuthority("ROLE_TEACHER"); // Require login for all POST requests to /api/assignment/**
                    registry.requestMatchers(HttpMethod.GET, "/api/assignments/displayAssignments/{studentId}").hasAuthority("ROLE_STUDENT");
                    registry.requestMatchers(HttpMethod.PUT, "api/assignments/updateAssignment/{assignmentId}").hasAuthority("ROLE_TEACHER");
                    registry.requestMatchers(HttpMethod.DELETE, "api/assignments/deleteAssignment/{assignmentId}").hasAuthority("ROLE_TEACHER");
                    registry.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.permitAll())
                .build();
    }

    /*
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET","POST"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
    */
}
