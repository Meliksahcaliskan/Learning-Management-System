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

    public void checkRateLimit(String clientIp) {
        try {
            if (isBlocked(clientIp)) {
                long remainingBlockTime = getRemainingBlockTime(clientIp);
                throw new RateLimitExceededException(
                        String.format("Too many attempts. Please try again in %d minutes",
                                remainingBlockTime)
                );
            }

            AtomicInteger attempts;
            try {
                attempts = attemptCache.get(clientIp);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            if (attempts.incrementAndGet() > maxAttempts) {
                blockCache.put(clientIp, Instant.now());
                throw new RateLimitExceededException(
                        String.format("Too many attempts. Please try again in %d minutes",
                                blockDurationMinutes)
                );
            }
        } catch (Exception e) {
            log.error("Error checking rate limit", e);
            // Allow the request if there's an error checking the rate limit
        }
    }

    public void resetLimit(String clientIp) {
        attemptCache.invalidate(clientIp);
        blockCache.invalidate(clientIp);
    }

    private boolean isBlocked(String clientIp) {
        try {
            Instant blockTime = blockCache.get(clientIp);
            return blockTime != null &&
                    blockTime.plus(blockDurationMinutes, ChronoUnit.MINUTES)
                            .isAfter(Instant.now());
        } catch (ExecutionException e) {
            return false;
        }
    }

    private long getRemainingBlockTime(String clientIp) {
        try {
            Instant blockTime = blockCache.get(clientIp);
            if (blockTime != null) {
                Instant unblockTime = blockTime.plus(blockDurationMinutes, ChronoUnit.MINUTES);
                return Duration.between(Instant.now(), unblockTime).toMinutes();
            }
        } catch (ExecutionException e) {
            log.error("Error getting remaining block time", e);
        }
        return 0;
    }
}
