package dev.glabay.controllers;

import dev.glabay.dtos.UserCredentialsDto;
import dev.glabay.models.request.RegistrationStatus;
import dev.glabay.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null)
            model.addAttribute("error", "Invalid email or password");
        return "login";
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
}
