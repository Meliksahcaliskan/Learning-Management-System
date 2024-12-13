package com.lsm.config;

import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.TestingAuthenticationToken;

@TestConfiguration
public class SecurityConfigTest {
    @Bean
    public TestingAuthenticationToken testAuthToken(AppUser appUser) {
        return new TestingAuthenticationToken(appUser, null, "ROLE_TEACHER");
    }

    @Bean
    public AppUser testAppUser() {
        return AppUser.builder()
                .id(1L)
                .username("teacher")
                .role(Role.ROLE_TEACHER)
                .build();
    }
}
