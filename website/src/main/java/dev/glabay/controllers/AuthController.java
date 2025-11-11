package dev.glabay.controllers;

import dev.glabay.dtos.UserCredentialsDto;
import dev.glabay.models.request.RegistrationStatus;
import dev.glabay.security.jwt.JwtService;
import dev.glabay.security.login.LoginAttemptService;
import dev.glabay.security.refresh.RefreshTokenService;
import dev.glabay.security.web.CookieHelper;
import dev.glabay.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import dev.glabay.feaures.users.CustomUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authService;
    private final CustomUserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CookieHelper cookieHelper;
    private final LoginAttemptService loginAttemptService;

    @Value("${jwt.access-ttl-minutes}")
    private int accessTtlMinutes;
    @Value("${jwt.refresh-ttl-days}")
    private int refreshTtlDays;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Model model,
                            HttpServletRequest request) {
        // If already authenticated via a valid access token, redirect to appropriate dashboard
        var atCookie = cookieHelper.getCookie(request, CookieHelper.ACCESS_COOKIE);
        if (atCookie.isPresent() && jwtService.isTokenValid(atCookie.get())) {
            var claims = jwtService.parseClaims(atCookie.get());
            var roles = jwtService.extractRoles(claims);
            String dest = destinationForRoles(roles);
            return "redirect:" + dest;
        }
        if (error != null)
            model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @PostMapping(value = "/login", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Void> performLogin(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestParam("email") String email,
        @RequestParam("password") String password
    ) {
        var ip = request.getRemoteAddr();
        if (loginAttemptService.isLocked(email, ip)) {
            return ResponseEntity.status(HttpStatus.LOCKED).build(); // Locked
        }
        try {
            var user = userDetailsService.loadUserByUsername(email);
            if (!passwordEncoder.matches(password, user.getPassword())) {
                loginAttemptService.onFailure(email, ip);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // success
            loginAttemptService.onSuccess(email, ip);
            var roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

            var accessToken = jwtService.generateAccessToken(email, email, roles);
            var issued = refreshTokenService.issue(email, request.getHeader("User-Agent"), ip);

            cookieHelper.setAccessCookie(response, accessToken, Duration.ofMinutes(accessTtlMinutes));
            cookieHelper.setRefreshCookie(response, issued.rawToken(), Duration.ofDays(refreshTtlDays));

            // For browser form submissions, redirect to role-based dashboard; for API/XHR keep 204
            if (isFormLike(request)) {
                String dest = destinationForRoles(roles);
                return ResponseEntity.status(HttpStatus.SEE_OTHER) // See Other
                    .header("Location", dest)
                    .build();
            }
            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            // user not found or other error
            loginAttemptService.onFailure(email, ip);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    @ResponseBody
    public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        var rawRefreshOpt = cookieHelper.getCookie(request, CookieHelper.REFRESH_COOKIE);
        if (rawRefreshOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var result = refreshTokenService.rotate(rawRefreshOpt.get(), request.getHeader("User-Agent"), request.getRemoteAddr());
        if (!result.success) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Resolve user email by validating the new token
        var active = refreshTokenService.validateActive(result.newRawToken);
        if (active.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var email = active.get().userEmail();
        // Load authorities for access token
        var user = userDetailsService.loadUserByUsername(email);
        var roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        var access = jwtService.generateAccessToken(email, email, roles);

        cookieHelper.setAccessCookie(response, access, Duration.ofMinutes(accessTtlMinutes));
        cookieHelper.setRefreshCookie(response, result.newRawToken, Duration.ofDays(refreshTtlDays));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        var rawRefreshOpt = cookieHelper.getCookie(request, CookieHelper.REFRESH_COOKIE);
        rawRefreshOpt.ifPresent(refreshTokenService::revokeCurrent);
        cookieHelper.clearAuthCookies(response);
        // For browser form submissions, redirect to home; for API/XHR keep 204
        if (isFormLike(request)) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "/home")
                .build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var name = authentication.getName();
        var roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return ResponseEntity.ok(new MeResponse(name, roles));
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("newUser", new UserCredentialsDto("", "", "", "", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(HttpServletRequest httpRequest, @ModelAttribute("newUser") UserCredentialsDto request, Model model) {
        var status = authService.registerUser(request, httpRequest.getRemoteAddr());
        if (status.equals(RegistrationStatus.CREATED))
            return "redirect:/auth/login?registered";
        if (status.equals(RegistrationStatus.ALREADY_EXISTS))
            model.addAttribute("error", "Email already in use");
        if (status.equals(RegistrationStatus.FAILED))
            model.addAttribute("error", "Failed to register");
        return "register";
    }

    private boolean isFormLike(HttpServletRequest request) {
        var contentType = request.getContentType();
        var accept = request.getHeader("Accept");
        var xrw = request.getHeader("X-Requested-With");
        var formContent = contentType != null && contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        var htmlAccept = accept != null && accept.contains("text/html");
        var isAjax = "XMLHttpRequest".equalsIgnoreCase(xrw);
        return formContent || (htmlAccept && !isAjax);
    }

    private String destinationForRoles(List<String> roles) {
        if (roles == null) return "/dashboard";
        var isTech = roles.stream().anyMatch(r -> "ROLE_TECHNICIAN".equals(r) || "TECHNICIAN".equals(r));
        return isTech ? "/dashboard/ticketing" : "/dashboard";
    }

    private record MeResponse(String email, List<String> roles) {}
}
