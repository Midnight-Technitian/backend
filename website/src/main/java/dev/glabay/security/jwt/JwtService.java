package dev.glabay.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service for generating and validating JSON Web Tokens (JWT) for browser clients.
 *
 * Token model:
 * - HS256 signing using a strong base64-encoded secret from environment/config
 * - Access token contains roles for authorization checks
 */
@Service
@NullMarked
public class JwtService {

    private final String issuer;
    private final String audience;
    private final int accessTtlMinutes;
    private final long clockSkewSeconds;
    private final Key signingKey;

    public JwtService(
        @Value("${jwt.issuer}") String issuer,
        @Value("${jwt.audience}") String audience,
        @Value("${jwt.access-ttl-minutes}") int accessTtlMinutes,
        @Value("${jwt.clock-skew-seconds}") long clockSkewSeconds,
        @Value("${jwt.secret}") String secret
    ) {
        this.issuer = issuer;
        this.audience = audience;
        this.accessTtlMinutes = accessTtlMinutes;
        this.clockSkewSeconds = clockSkewSeconds;
        if (secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set jwt.secret or JWT_SECRET env var.");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secret.trim());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String subject, String email, Collection<String> roles) {
        var now = Instant.now();
        var exp = now.plus(accessTtlMinutes, ChronoUnit.MINUTES);

        var claims = new HashMap<String, Object>();
            claims.put("email", email);
            claims.put("roles", new ArrayList<>(roles));

        return Jwts.builder()
            .setIssuer(issuer)
            .setAudience(audience)
            .setSubject(subject)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(exp))
            .addClaims(claims)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            var jws = Jwts.parserBuilder()
                .requireIssuer(issuer)
                .requireAudience(audience)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);

            // Exp is checked by the parser; additional custom checks can go here.
            return jws != null;
        }
        catch (Exception e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .requireIssuer(issuer)
            .requireAudience(audience)
            .setAllowedClockSkewSeconds(clockSkewSeconds)
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Claims claims) {
        Object raw = claims.get("roles");
        if (raw instanceof List<?>) {
            List<?> list = (List<?>) raw;
            List<String> roles = new ArrayList<>();
            for (Object o : list) {
                if (o != null) roles.add(o.toString());
            }
            return roles;
        }
        if (raw instanceof String s) {
            return Arrays.asList(s.split(","));
        }
        return Collections.emptyList();
    }

    public Optional<String> extractEmail(Claims claims) {
        Object email = claims.get("email");
        return Optional.ofNullable(email == null ? null : email.toString());
    }
}
