package dev.glabay.service;

import dev.glabay.dtos.ServiceDto;
import dev.glabay.inter.DtoConverter;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public interface ServiceConverter extends DtoConverter<dev.glabay.service.model.Service, ServiceDto> {
    @Override
    default ServiceDto mapToDto(dev.glabay.service.model.Service model) {
        return new ServiceDto(
            model.getServiceId(),
            model.getServiceName(),
            model.getServiceDescription(),
            model.getServicePrice(),
            model.isFixedRate()
        );
    }
}
