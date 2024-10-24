package com.lsm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class LsmApplicationTests {

    @BeforeAll
    static void setUp() {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();
        
        // Set system properties from the .env file
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }

    @Test
    void contextLoads() {}
}