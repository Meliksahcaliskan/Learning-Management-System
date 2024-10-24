package com.lsm.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lsm.security.JwtAuthenticationFilter;
import com.lsm.service.AppUserService;
import com.lsm.service.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @Mock
    private AppUserService appUserService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpSecurity httpSecurity;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(appUserService);
    }

    @Test
    void userDetailsService_ShouldReturnAppUserService() {
        assertEquals(appUserService, securityConfig.userDetailsService());
    }

    @Test
    void passwordEncoder_ShouldNotBeNull() {
        assertNotNull(securityConfig.passwordEncoder());
    }

    /*
    @Test
    void authenticationProvider_ShouldReturnConfiguredProvider() {
        DaoAuthenticationProvider provider = securityConfig.authenticationProvider();
        
        assertNotNull(provider);
        assertEquals(appUserService, provider.getUserDetailsService());
        assertNotNull(provider.getPasswordEncoder());
    }
    */

    @Test
    void authenticationManager_ShouldReturnFromConfiguration() throws Exception {
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(result);
        assertEquals(mockAuthManager, result);
    }

    @Test
    void passwordEncoder_ShouldBeBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        
        assertNotNull(encoder);
        assertTrue(encoder.matches("password", encoder.encode("password")));
    }

    @Test
    void securityFilterChain_ShouldConfigureSecurityCorrectly() throws Exception {
        // Mock behavior for HttpSecurity
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity); // Ensure csrf returns the same mock
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity); // Ensure authorizeHttpRequests returns the same mock
        when(httpSecurity.addFilterBefore(any(JwtAuthenticationFilter.class), eq(UsernamePasswordAuthenticationFilter.class)))
            .thenReturn(httpSecurity); // Ensure addFilterBefore returns the same mock

        // Call the method under test
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity, jwtAuthenticationFilter);

        // Assert that the filter chain is not null (ensuring that it is built correctly)
        assertNotNull(filterChain);

        // Verify interactions with the mocks
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}