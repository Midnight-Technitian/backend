package dev.glabay.security.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@NullMarked
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        var accept = request.getHeader("Accept");
        var path = request.getRequestURI();
        boolean wantsJson = path.startsWith("/api/")
            || (accept != null && accept.contains("application/json"))
            || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (wantsJson) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Authentication required\"}");
        }
        else {
            response.sendRedirect("/auth/login");
        }
    }
}
