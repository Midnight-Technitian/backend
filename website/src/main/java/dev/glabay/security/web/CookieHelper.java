package dev.glabay.security.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

/**
 * Helper for issuing and clearing HttpOnly Secure cookies with SameSite.
 */
@Component
public class CookieHelper {

    public static final String ACCESS_COOKIE = "mt_at";
    public static final String REFRESH_COOKIE = "mt_rt";
    public static final String XSRF_COOKIE = "XSRF-TOKEN";

    private final boolean secureCookies;
    private final String cookieDomain; // nullable

    public CookieHelper(
        @Value("${security.cookies.secure:true}") boolean secureCookies,
        @Value("${security.cookies.domain:}") String cookieDomain
    ) {
        this.secureCookies = secureCookies;
        this.cookieDomain = (cookieDomain == null || cookieDomain.isBlank()) ? null : cookieDomain;
    }

    public void setAccessCookie(HttpServletResponse response, String token, Duration ttl) {
        ResponseCookie cookie = baseCookieBuilder(ACCESS_COOKIE, token)
            .path("/")
            .maxAge(ttl)
            .httpOnly(true)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void setRefreshCookie(HttpServletResponse response, String token, Duration ttl) {
        // Scope refresh cookie to /auth
        ResponseCookie cookie = baseCookieBuilder(REFRESH_COOKIE, token)
            .path("/auth")
            .maxAge(ttl)
            .httpOnly(true)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        // Clear access token at common paths to cover any legacy path differences
        ResponseCookie atRoot = baseCookieBuilder(ACCESS_COOKIE, "")
            .path("/")
            .maxAge(Duration.ZERO)
            .httpOnly(true)
            .build();
        ResponseCookie atAuth = baseCookieBuilder(ACCESS_COOKIE, "")
            .path("/auth")
            .maxAge(Duration.ZERO)
            .httpOnly(true)
            .build();

        // Clear refresh token at both /auth (current) and / (legacy safeguard)
        ResponseCookie rtAuth = baseCookieBuilder(REFRESH_COOKIE, "")
            .path("/auth")
            .maxAge(Duration.ZERO)
            .httpOnly(true)
            .build();
        ResponseCookie rtRoot = baseCookieBuilder(REFRESH_COOKIE, "")
            .path("/")
            .maxAge(Duration.ZERO)
            .httpOnly(true)
            .build();

        response.addHeader("Set-Cookie", atRoot.toString());
        response.addHeader("Set-Cookie", atAuth.toString());
        response.addHeader("Set-Cookie", rtAuth.toString());
        response.addHeader("Set-Cookie", rtRoot.toString());
    }

    public Optional<String> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();
        return Arrays.stream(cookies)
            .filter(c -> name.equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookieBuilder(String name, String value) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(name, value)
            .secure(secureCookies)
            .sameSite("Lax");
        if (cookieDomain != null) b.domain(cookieDomain);
        return b;
    }
}
