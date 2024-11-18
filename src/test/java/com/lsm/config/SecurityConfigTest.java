package com.lsm.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
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

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private CsrfConfigurer<HttpSecurity> csrfConfigurer;

    @Mock
    private AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authRegistry;

    @Mock
    private DefaultSecurityFilterChain securityFilterChain;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() throws Exception {
        securityConfig = new SecurityConfig(appUserService);

        // Mock the builder pattern to return httpSecurity for method chaining
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(securityFilterChain);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void userDetailsService_ShouldReturnAppUserService() {
        assertEquals(appUserService, securityConfig.userDetailsService());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
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
    @MockitoSettings(strictness = Strictness.LENIENT)
    void authenticationManager_ShouldReturnFromConfiguration() throws Exception {
        AuthenticationManager mockAuthManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(mockAuthManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(result);
        assertEquals(mockAuthManager, result);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void passwordEncoder_ShouldBeBCryptPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        
        assertNotNull(encoder);
        assertTrue(encoder.matches("password", encoder.encode("password")));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @SuppressWarnings("unchecked")
    void securityFilterChain_ShouldConfigureSecurityCorrectly() throws Exception {
        // Arrange: Mock logout with the correct type
        when(httpSecurity.logout(any(Customizer.class))).thenAnswer(invocation -> httpSecurity);
        
        // Act
        SecurityFilterChain result = securityConfig.securityFilterChain(httpSecurity, jwtAuthenticationFilter);

        // Assert
        assertNotNull(result);
        
        // Verify all security configurations were called
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).addFilterBefore(
            eq(jwtAuthenticationFilter), 
            eq(UsernamePasswordAuthenticationFilter.class)
        );
        verify(httpSecurity).logout(any());  // Ensure logout is configured
        verify(httpSecurity).build();
    }



}