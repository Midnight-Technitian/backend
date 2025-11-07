package dev.glabay.email.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private final Cache<String, Boolean> processedCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(24))
        .maximumSize(100_000)
        .build();

    public boolean isProcessed(String emailId) {
        Boolean processed = processedCache.getIfPresent(emailId);
        return processed != null && processed;
    }

    public void markProcessed(String emailId) {
        processedCache.put(emailId, true);
    }
}
