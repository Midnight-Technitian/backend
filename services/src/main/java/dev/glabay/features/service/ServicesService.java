package dev.glabay.features.service;

import dev.glabay.dtos.ServiceDto;
import dev.glabay.inter.impl.ServiceConverter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
@Service
public class ServicesService implements ServiceConverter {
    private final ServiceRepository serviceRepository;

    public ServicesService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceDto> getAllServices() {
        var services = serviceRepository.findAll();
        return services.stream().map(this::mapToDto).toList();
    }

    public ServiceDto getServiceById(Long serviceId) {
        return serviceRepository.findById(serviceId)
                .map(this::mapToDto)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with ID: " + serviceId));
    }
}
