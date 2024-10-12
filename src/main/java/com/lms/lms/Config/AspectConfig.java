package com.lms.lms.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lms.lms.Aspect.LoggingAspect;

@Configuration
public class AspectConfig {
    
    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
