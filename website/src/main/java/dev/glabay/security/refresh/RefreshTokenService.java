package dev.glabay.security.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository repository;
    private final SecureTokenGenerator tokenGenerator;
    private final TokenHashingService hashingService;

    public RefreshTokenService(
        RefreshTokenRepository repository,
        SecureTokenGenerator tokenGenerator,
        TokenHashingService hashingService,
        @Value("${jwt.refresh-ttl-days}") int refreshTtlDays,
        @Value("${security.refresh.detect-reuse:true}") boolean detectReuse,
        @Value("${security.refresh.revoke-family-on-reuse:true}") boolean revokeFamilyOnReuse,
        @Value("${security.refresh.log-reuse:true}") boolean logReuse,
        @Value("${security.refresh.cleanup-expired:false}") boolean cleanupExpired,
        @Value("${security.refresh.cleanup-grace-days:7}") int cleanupGraceDays
    ) {
        this.repository = repository;
        this.tokenGenerator = tokenGenerator;
        this.hashingService = hashingService;
        this.refreshTtlDays = refreshTtlDays;
        this.detectReuse = detectReuse;
        this.revokeFamilyOnReuse = revokeFamilyOnReuse;
        this.logReuse = logReuse;
        this.cleanupExpired = cleanupExpired;
        this.cleanupGraceDays = cleanupGraceDays;
    }

    private final int refreshTtlDays;
    private final boolean detectReuse;
    private final boolean revokeFamilyOnReuse;
    private final boolean logReuse;
    private final boolean cleanupExpired;
    private final int cleanupGraceDays;

    @Transactional
    public IssueResult issue(String userEmail, String userAgent, String ip) {
        var now = LocalDateTime.now();
        var expiresAt = now.plusDays(refreshTtlDays);

        var raw = tokenGenerator.generate256Bit();
        var hash = hashingService.hash(raw);

        var entity = new RefreshToken();
            entity.setId(UUID.randomUUID());
            entity.setUserEmail(userEmail);
            entity.setTokenHash(hash);
            entity.setIssuedAt(now);
            entity.setExpiresAt(expiresAt);
            entity.setUserAgent(userAgent);
            entity.setIp(ip);

        repository.save(entity);
        return new IssueResult(entity.getId(), raw, expiresAt);
    }

    /**
     * Validate that the provided raw token exists, is not revoked, and not expired.
     */
    @Transactional(readOnly = true)
    public Optional<ActiveToken> validateActive(String rawToken) {
        var hash = hashingService.hash(rawToken);
        var found = repository.findByTokenHashAndRevokedAtIsNull(hash);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        var token = found.get();
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }
        return Optional.of(new ActiveToken(token.getId(), token.getUserEmail(), token.getExpiresAt()));
    }

    /**
     * Rotate the provided raw refresh token, returning a new raw token and revoking the old one.
     * If reuse of a previously rotated token is detected (presented token is already revoked),
     * the whole replacement chain (family) will be revoked if configured to do so.
     */
    @Transactional
    public RotateResult rotate(String rawToken, String userAgent, String ip) {
        var hash = hashingService.hash(rawToken);
        var opt = repository.findByTokenHash(hash);
        if (opt.isEmpty()) {
            // Unknown token; treat as invalid
            return RotateResult.invalid();
        }
        var current = opt.get();
        var now = LocalDateTime.now();

        if (current.getRevokedAt() != null) {
            if (detectReuse) {
                if (logReuse) {
                    log.warn("Refresh token reuse detected for user {} starting from token {}", current.getUserEmail(), current.getId());
                }
                if (revokeFamilyOnReuse) {
                    revokeFamilyFrom(current.getId());
                }
            }
            return RotateResult.reused();
        }

        if (current.getExpiresAt().isBefore(now)) {
            // expired
            current.setRevokedAt(now);
            repository.save(current);
            return RotateResult.expired();
        }

        // Create a new token and link it
        var issued = issue(current.getUserEmail(), userAgent, ip);

        // revoke current and link to new id
        current.setRevokedAt(now);
        current.setReplacedBy(issued.tokenId());
        repository.save(current);

        return RotateResult.rotated(issued.tokenId(), issued.rawToken(), issued.expiresAt());
    }

    @Transactional
    public boolean revokeCurrent(String rawToken) {
        String hash = hashingService.hash(rawToken);
        Optional<RefreshToken> opt = repository.findByTokenHash(hash);
        if (opt.isEmpty()) return false;
        RefreshToken t = opt.get();
        if (t.getRevokedAt() == null) {
            t.setRevokedAt(LocalDateTime.now());
            repository.save(t);
            return true;
        }
        return false;
    }

    @Transactional
    public int revokeFamily(UUID startId) {
        return revokeFamilyFrom(startId);
    }

    private int revokeFamilyFrom(UUID startId) {
        int count = 0;
        UUID cursor = startId;
        LocalDateTime now = LocalDateTime.now();
        while (cursor != null) {
            Optional<RefreshToken> opt = repository.findById(cursor);
            if (opt.isEmpty()) break;
            RefreshToken t = opt.get();
            if (t.getRevokedAt() == null) {
                t.setRevokedAt(now);
                repository.save(t);
                count++;
            }
            cursor = t.getReplacedBy();
        }
        return count;
    }

    /** Optional cleanup to hard-delete expired tokens after a grace period. */
    @Transactional
    public int cleanupExpired() {
        if (!cleanupExpired) return 0;
        LocalDateTime threshold = LocalDateTime.now().minus(cleanupGraceDays, ChronoUnit.DAYS);
        int removed = 0;
        for (RefreshToken t : repository.findAllExpired(threshold)) {
            try {
                repository.delete(t);
                removed++;
            } catch (Exception ignored) { }
        }
        return removed;
    }

    // Value-like return types using Java records
    public record IssueResult(UUID tokenId, String rawToken, LocalDateTime expiresAt) {}

    public static final class RotateResult {
        public final boolean success;
        public final boolean invalid;
        public final boolean reused;
        public final boolean expired;
        public final UUID newTokenId;
        public final String newRawToken;
        public final LocalDateTime newExpiresAt;

        private RotateResult(boolean success, boolean invalid, boolean reused, boolean expired,
                              UUID newTokenId, String newRawToken, LocalDateTime newExpiresAt) {
            this.success = success;
            this.invalid = invalid;
            this.reused = reused;
            this.expired = expired;
            this.newTokenId = newTokenId;
            this.newRawToken = newRawToken;
            this.newExpiresAt = newExpiresAt;
        }

        public static RotateResult rotated(UUID id, String raw, LocalDateTime exp) {
            return new RotateResult(true, false, false, false, id, raw, exp);
        }
        public static RotateResult invalid() {
            return new RotateResult(false, true, false, false, null, null, null);
        }
        public static RotateResult reused() {
            return new RotateResult(false, false, true, false, null, null, null);
        }
        public static RotateResult expired() {
            return new RotateResult(false, false, false, true, null, null, null);
        }
    }

    public record ActiveToken(UUID tokenId, String userEmail, LocalDateTime expiresAt) {}
}
