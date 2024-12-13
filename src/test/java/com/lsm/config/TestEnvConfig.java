package com.lsm.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class TestEnvConfig {

    @PostConstruct
    public void init() {
        System.setProperty("SPRING_APPLICATION_NAME", "lsm-test");
        // Add any other required environment variables
        System.setProperty("SERVER_PORT", "8081");
        System.setProperty("SPRING_DATASOURCE_URL", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        System.setProperty("SPRING_DATASOURCE_USERNAME", "sa");
        System.setProperty("SPRING_DATASOURCE_PASSWORD", "");
    }
}
