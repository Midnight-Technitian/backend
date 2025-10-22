package dev.glabay.security;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-09-09
 */
@Component
public class BotTokenAuthInterceptor implements HandlerInterceptor {

    private final ApiTokenProperties props;

    private byte[] tokenBytes;
    private byte[] nextTokenBytes;

    public BotTokenAuthInterceptor(ApiTokenProperties props) {
        this.props = props;
    }

    @PostConstruct
    void init() {
        this.tokenBytes = toBytesOrNull(props.getSecret());
        this.nextTokenBytes = toBytesOrNull(props.getNextSecret());
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
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || auth.isBlank() || !auth.startsWith("Bearer ")) {
            unauthorized(response);
            return false;
        }
        String raw = auth.substring("Bearer ".length());
        byte[] rawBytes = raw.getBytes(StandardCharsets.UTF_8);

        boolean ok = equalsConstantTime(rawBytes, tokenBytes) ||
            equalsConstantTime(rawBytes, nextTokenBytes);

        if (!ok) {
            unauthorized(response);
            return false;
        }
        return true;
    }

    private void unauthorized(HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
    }
}

