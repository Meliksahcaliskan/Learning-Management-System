package com.lsm.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rate.limit")
@Component
@Getter
@Setter
public class RateLimitProperties {
    private boolean enabled = true;
    private int maxAttempts = 5;
    private int duration = 15;  // minutes
}
