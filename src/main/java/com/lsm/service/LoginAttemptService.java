package com.lsm.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginAttemptService {
    @Value("${security.login-attempts.max-attempts}")
    private int cacheMaxSize;

    @Value("${security.login-attempts.block-duration-minutes}")
    private int blockDurationMinutes;

    private final LoadingCache<String, Integer> attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(blockDurationMinutes, TimeUnit.MINUTES)
            .maximumSize(cacheMaxSize)
            .recordStats()
            .build(new CacheLoader<>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });

    public void loginSucceeded(String key) {
        try {
            attemptsCache.invalidate(key);
            log.debug("Login attempts reset for key: {}", key);
        } catch (Exception e) {
            log.error("Error resetting login attempts for key: {}", key, e);
        }
    }

    public void loginFailed(String key) {
        try {
            int attempts = attemptsCache.get(key);
            attempts++;
            attemptsCache.put(key, attempts);
            log.debug("Login failed for key: {}. Attempts: {}", key, attempts);

            if (attempts >= AuthService.MAX_LOGIN_ATTEMPTS) {
                log.warn("Account blocked for key: {} due to too many failed attempts", key);
            }
        } catch (ExecutionException e) {
            log.error("Error recording failed login attempt for key: {}", key, e);
            attemptsCache.put(key, 1);
        }
    }

    public boolean isBlocked(String key) {
        try {
            int attempts = attemptsCache.get(key);
            boolean blocked = attempts >= AuthService.MAX_LOGIN_ATTEMPTS;
            if (blocked) {
                log.debug("Access blocked for key: {}. Current attempts: {}", key, attempts);
            }
            return blocked;
        } catch (ExecutionException e) {
            log.error("Error checking blocked status for key: {}", key, e);
            return false;
        }
    }

    public int getCurrentAttempts(String key) {
        try {
            return attemptsCache.get(key);
        } catch (ExecutionException e) {
            log.error("Error getting current attempts for key: {}", key, e);
            return 0;
        }
    }

    public long getRemainingBlockTime(String key) {
        try {
            if (!isBlocked(key)) {
                return 0;
            }

            // This is an approximation since we don't store the exact block time
            return AuthService.LOCK_DURATION;
        } catch (Exception e) {
            log.error("Error getting remaining block time for key: {}", key, e);
            return 0;
        }
    }

    // For testing and administrative purposes
    public void resetAttempts(String key) {
        attemptsCache.invalidate(key);
        log.info("Login attempts manually reset for key: {}", key);
    }

    public void clearAll() {
        attemptsCache.invalidateAll();
        log.info("All login attempts cleared");
    }
}
