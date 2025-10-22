package dev.glabay.features.devices;

import dev.glabay.dtos.CustomerDeviceDto;
import dev.glabay.inter.impl.CustomerDeviceConverter;
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
public class CustomerDeviceService implements CustomerDeviceConverter {

    private final CustomerDeviceRepository customerDeviceRepository;

    public List<CustomerDeviceDto> getAllCustomerDevices() {
        return customerDeviceRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .toList();
    }

    public List<CustomerDeviceDto> getCustomerDevices(Integer customerId) {
        return customerDeviceRepository.findByCustomerId(customerId)
            .stream()
            .map(this::mapToDto)
            .toList();
    }
}
