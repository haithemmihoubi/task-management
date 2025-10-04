package com.haithem.taskmanagemnt.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting configuration using Bucket4j
 * Implements token bucket algorithm for API rate limiting
 */
@Configuration
public class RateLimitConfig {

    /**
     * Thread-safe map to store buckets per IP address
     */
    @Bean
    public Map<String, Bucket> bucketCache() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Create a bucket with rate limit: 100 requests per minute
     */
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

