package dev.glabay.security.refresh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Hashes refresh tokens using HMAC-SHA256 with a server-side secret (pepper).
 * Only the hash is stored in DB. Raw refresh tokens are never persisted.
 */
@Service
public class TokenHashingService {
    private static final Logger log = LoggerFactory.getLogger(TokenHashingService.class);

    private final byte[] hmacKey;

    public TokenHashingService(
        @Value("${refresh.pepper}") String pepper,
        @Value("${jwt.secret}") String jwtSecret
    ) {
        String chosen = (pepper != null && !pepper.isBlank()) ? pepper : jwtSecret;
        if (chosen == null || chosen.isBlank()) {
            throw new IllegalStateException("Refresh token pepper/secret is not configured. Set REFRESH_TOKEN_PEPPER or JWT_SECRET.");
        }
        // Accept either raw or base64 value; try base64 decode first, fallback to UTF-8 bytes if invalid
        byte[] keyCandidate;
        try {
            keyCandidate = Base64.getDecoder().decode(chosen.trim());
        }
        catch (IllegalArgumentException e) {
            keyCandidate = chosen.getBytes(StandardCharsets.UTF_8);
        }
        this.hmacKey = keyCandidate;
    }

    public String hash(String rawToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacKey, "HmacSHA256"));
            byte[] out = mac.doFinal(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(out);
        } catch (Exception e) {
            log.error("Failed to hash refresh token: {}", e.getMessage());
            throw new IllegalStateException("Hashing error", e);
        }
    }
}
