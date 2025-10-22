package dev.glabay.features.service;

import dev.glabay.dtos.ServiceDto;
import dev.glabay.inter.impl.ServiceConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
@Service
@RequiredArgsConstructor
public class ServicesService implements ServiceConverter {
    private final ServiceRepository serviceRepository;

    public List<ServiceDto> getAllServices() {
        return serviceRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .toList();
    }
}
