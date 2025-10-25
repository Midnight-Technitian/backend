package dev.glabay.inter.impl;


import dev.glabay.dtos.CustomerDeviceDto;
import dev.glabay.inter.DtoConverter;
import dev.glabay.features.devices.CustomerDevice;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public interface CustomerDeviceConverter extends DtoConverter<CustomerDevice, CustomerDeviceDto> {

    default CustomerDeviceDto mapToDto(CustomerDevice model) {
        return new CustomerDeviceDto(
            model.getDeviceId(),
            model.getCustomerEmail(),
            model.getCreatedAt(),
            model.getUpdatedAt(),
            model.getDeviceName(),
            model.getDeviceType(),
            model.getDeviceInfo()
        );
    }
}
