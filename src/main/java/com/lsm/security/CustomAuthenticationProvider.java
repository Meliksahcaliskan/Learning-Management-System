package com.lsm.security;

import com.lsm.repository.AppUserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

@Component
@Primary
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private final AppUserRepository userRepository;

    public CustomAuthenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            AppUserRepository userRepository) {

        super();
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
        this.setHideUserNotFoundExceptions(true);
        this.userRepository = userRepository;
    }

    @Override
    protected void additionalAuthenticationChecks(
            org.springframework.security.core.userdetails.UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        super.additionalAuthenticationChecks(userDetails, authentication);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication,
                                                         org.springframework.security.core.userdetails.UserDetails user) {
        return super.createSuccessAuthentication(principal, authentication, user);
    }
}