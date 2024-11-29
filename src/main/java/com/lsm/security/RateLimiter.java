package com.lsm.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lsm.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class RateLimiter {
    private final LoadingCache<String, AtomicInteger> attemptCache;
    private final LoadingCache<String, Instant> blockCache;

    @Value("${rate.limit.max-attempts}")
    private int maxAttempts;

    @Value("${rate.limit.duration}")
    private int blockDurationMinutes;

    public RateLimiter() {
        attemptCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public AtomicInteger load(String key) {
                        return new AtomicInteger(0);
                    }
                });

        blockCache = CacheBuilder.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public Instant load(String key) {
                        return Instant.now();
                    }
                });
    }

    public void checkRateLimit(String clientIp) throws RateLimitExceededException {
        try {
            // First check if the IP is blocked
            if (isBlocked(clientIp)) {
                long remainingTime = getRemainingBlockTime(clientIp);
                throw new RateLimitExceededException(
                        String.format("Too many attempts. Please try again in %d minutes", remainingTime)
                );
            }

            // Get current attempt count
            AtomicInteger attempts = attemptCache.get(clientIp);
            int currentAttempts = attempts.incrementAndGet();

            // If exceeded max attempts, block the IP
            if (currentAttempts > maxAttempts) {
                blockCache.put(clientIp, Instant.now());
                attempts.set(maxAttempts + 1); // Ensure it stays over limit
                throw new RateLimitExceededException(
                        String.format("Too many attempts. Please try again in %d minutes", blockDurationMinutes)
                );
            }

        } catch (ExecutionException e) {
            // Only catch cache-related exceptions
            log.error("Cache error in rate limiter", e);
            throw new RuntimeException("Error checking rate limit", e);
        }
    }

    public void resetLimit(String clientIp) {
        try {
            attemptCache.get(clientIp).set(0);
            blockCache.invalidate(clientIp);
        } catch (ExecutionException e) {
            log.error("Error resetting rate limit for IP: {}", clientIp, e);
        }
    }

    private boolean isBlocked(String clientIp) throws ExecutionException {
        Instant blockTime = blockCache.get(clientIp);
        return blockTime != null &&
                blockTime.plus(blockDurationMinutes, ChronoUnit.MINUTES).isAfter(Instant.now());
    }

    private long getRemainingBlockTime(String clientIp) throws ExecutionException {
        Instant blockTime = blockCache.get(clientIp);
        if (blockTime != null) {
            Instant unblockTime = blockTime.plus(blockDurationMinutes, ChronoUnit.MINUTES);
            return Math.max(0, Duration.between(Instant.now(), unblockTime).toMinutes());
        }
        return 0;
    }
}