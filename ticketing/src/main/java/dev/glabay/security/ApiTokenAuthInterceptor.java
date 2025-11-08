package dev.glabay.security;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SealedObject;
import java.nio.charset.StandardCharsets;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-11-08
 */
@Component
public class ApiTokenAuthInterceptor implements HandlerInterceptor {
    private final ApiTokenProperties props;

    private byte[] tokenBytes;

    public ApiTokenAuthInterceptor(ApiTokenProperties props) {
        this.props = props;
    }

    @PostConstruct
    void init() {
        this.tokenBytes = toBytesOrNull(props.getSecret());
    }

    private static byte[] toBytesOrNull(String s) {
        return (s == null || s.isBlank()) ? null : s.getBytes(StandardCharsets.UTF_8);
    }

    private static boolean equalsConstantTime(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey == null || apiKey.isBlank()) {
            unauthorized(response);
            return false;
        }
        byte[] rawBytes = apiKey.getBytes(StandardCharsets.UTF_8);
        byte[] tokenCopy = (tokenBytes != null) ? tokenBytes.clone() : null;

        boolean ok = equalsConstantTime(rawBytes, tokenCopy);

        if (!ok) {
            unauthorized(response);
            return false;
        }
        return true;
    }

    private void unauthorized(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}

