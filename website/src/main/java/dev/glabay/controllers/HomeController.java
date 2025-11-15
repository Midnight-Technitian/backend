package dev.glabay.controllers;

import dev.glabay.dtos.ServiceDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@Controller
public class HomeController {
    private final RestClient restClient;

    public HomeController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping({"/", "/home", "/index"})
    public String getHomePage(Model model) {
        var serviceList = restClient.get()
            .uri("http://services/api/v1/services")
            .retrieve()
            .body(new ParameterizedTypeReference<List<ServiceDto>>() {});
        model.addAttribute("servicesOffered", serviceList);
        return "index";
    }
}
