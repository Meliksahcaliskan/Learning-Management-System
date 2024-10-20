package com.lsm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class LsmApplication {

	public static void main(String[] args) {
		// Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();
        
        // Set system properties from .env file
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        
		SpringApplication.run(LsmApplication.class, args);
	}

}
