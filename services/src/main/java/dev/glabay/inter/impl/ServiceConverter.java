package dev.glabay.inter.impl;

import dev.glabay.dtos.ServiceDto;
import dev.glabay.inter.DtoConverter;
import dev.glabay.features.service.Service;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public interface ServiceConverter extends DtoConverter<Service, ServiceDto> {
    @Override
    default ServiceDto mapToDto(Service model) {
        return new ServiceDto(
            model.serviceName(),
            model.serviceDescription(),
            model.servicePrice(),
            model.fixedRate()
        );
    }
}
