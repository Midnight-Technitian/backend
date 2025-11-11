package dev.glabay.security.refresh;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates cryptographically-strong random tokens suitable for use as refresh tokens.
 * Output is Base64 URL-safe without padding.
 */
@Component
public class SecureTokenGenerator {
    private final SecureRandom random = new SecureRandom();

    /**
     * Generate a 256-bit random token encoded as Base64URL without padding.
     */
    public String generate256Bit() {
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
