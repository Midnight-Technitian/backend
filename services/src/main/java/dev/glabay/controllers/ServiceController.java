package dev.glabay.controllers;

import dev.glabay.dtos.ServiceDto;
import dev.glabay.services.ServicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/services")
public class ServiceController {
    public final ServicesService servicesService;

    @GetMapping
    public List<ServiceDto> getAllServices() {
        return servicesService.getAllServices();
    }
}
