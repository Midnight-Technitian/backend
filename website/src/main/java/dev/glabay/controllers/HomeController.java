package dev.glabay.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@Controller
public class HomeController {

    @GetMapping({"/", "/home", "/index"})
    public String getHomePage() {
        return "index";
    }
}
