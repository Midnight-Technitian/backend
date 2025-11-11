package dev.glabay.security.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple in-memory brute-force defense for login attempts.
 *
 * Policy (configurable):
 * - After {@code maxAttempts} failed attempts, the account is locked for {@code lockoutMinutes} minutes.
 * - Keying strategy: by username; optionally include IP if {@code useIpFactor} is true.
 *
 * Note: This is in-memory and resets on application restart. For clustered deployments,
 * replace with a shared store (e.g., Redis) or database-backed implementation.
 */
@Service
public class LoginAttemptService {
    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    private final int maxAttempts;
    private final Duration lockoutDuration;
    private final boolean useIpFactor;

    private final ConcurrentMap<String, Entry> attempts = new ConcurrentHashMap<>();

    public LoginAttemptService(
        @Value("${security.login.max-attempts:${SECURITY_LOGIN_MAX_ATTEMPTS:3}}") int maxAttempts,
        @Value("${security.login.lock-minutes:${SECURITY_LOGIN_LOCK_MINUTES:5}}") int lockMinutes,
        @Value("${security.login.use-ip:false}") boolean useIpFactor
    ) {
        this.maxAttempts = Math.max(1, maxAttempts);
        this.lockoutDuration = Duration.ofMinutes(Math.max(1, lockMinutes));
        this.useIpFactor = useIpFactor;
    }

    public boolean isLocked(String username, String ip) {
        String key = key(username, ip);
        Entry e = attempts.get(key);
        if (e == null) return false;
        if (e.lockedUntil != null) {
            if (LocalDateTime.now().isAfter(e.lockedUntil)) {
                // unlock
                attempts.remove(key);
                return false;
            }
            return true;
        }
        return false;
    }

    public long remainingLockoutSeconds(String username, String ip) {
        String key = key(username, ip);
        Entry e = attempts.get(key);
        if (e == null || e.lockedUntil == null) return 0;
        long seconds = Duration.between(LocalDateTime.now(), e.lockedUntil).toSeconds();
        return Math.max(0, seconds);
    }

    public void onSuccess(String username, String ip) {
        String key = key(username, ip);
        attempts.remove(key);
    }

    public void onFailure(String username, String ip) {
        String key = key(username, ip);
        attempts.compute(key, (k, existing) -> {
            LocalDateTime now = LocalDateTime.now();
            if (existing == null) {
                existing = new Entry(1, now, null);
            } else {
                if (existing.lockedUntil != null && now.isBefore(existing.lockedUntil)) {
                    // still locked, keep state
                    return existing;
                }
                // not locked, increment counter
                existing = new Entry(existing.count + 1, existing.firstFailureAt, null);
            }

            if (existing.count >= maxAttempts) {
                LocalDateTime lockedUntil = now.plus(lockoutDuration);
                log.warn("Login lockout triggered for {} until {}", username, lockedUntil);
                return new Entry(existing.count, existing.firstFailureAt, lockedUntil);
            }
            return existing;
        });
    }

    private String key(String username, String ip) {
        if (useIpFactor && ip != null && !ip.isBlank()) {
            return username + "|" + ip;
        }
        return username;
    }

    private record Entry(int count, LocalDateTime firstFailureAt, LocalDateTime lockedUntil) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry entry)) return false;
            return count == entry.count
                && Objects.equals(firstFailureAt, entry.firstFailureAt)
                && Objects.equals(lockedUntil, entry.lockedUntil);
        }
        @Override
        public int hashCode() {
            return Objects.hash(count, firstFailureAt, lockedUntil);
        }
    }
}
