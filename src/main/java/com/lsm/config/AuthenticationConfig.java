package com.lsm.config;

import com.lsm.security.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.lsm.repository.AppUserRepository;

@Configuration
public class AuthenticationConfig {

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            AppUserRepository userRepository) {

        CustomAuthenticationProvider provider = new CustomAuthenticationProvider(
                userDetailsService,
                passwordEncoder,
                userRepository
        );

        // Preserve the hide user not found exceptions setting
        provider.setHideUserNotFoundExceptions(true);

        return provider;
    }
}