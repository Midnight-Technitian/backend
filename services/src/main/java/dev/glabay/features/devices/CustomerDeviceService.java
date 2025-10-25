package dev.glabay.features.devices;

import dev.glabay.dtos.CustomerDeviceDto;
import dev.glabay.features.customer.Customer;
import dev.glabay.inter.impl.CustomerDeviceConverter;
import dev.glabay.models.device.RegisteringDevice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<CustomerDeviceDto> getCustomerDevices(String email) {
        return customerDeviceRepository.findByCustomerEmailIgnoreCase(email)
            .stream()
            .map(this::mapToDto)
            .toList();
    }

    public CustomerDeviceDto createNewCustomerDevice(RegisteringDevice dto) {
        var device = new CustomerDevice();
            device.setCustomerEmail(dto.getCustomerEmail());
            device.setDeviceName(dto.getDeviceName());
            device.setDeviceType(dto.getDeviceType());
            device.setDeviceInfo(dto.getDeviceInfo());
            device.setCreatedAt(LocalDateTime.now());
            device.setUpdatedAt(LocalDateTime.now());
        var cached = saveCustomerDevice(device);
        return mapToDto(cached);
    }

    private CustomerDevice saveCustomerDevice(CustomerDevice device) {
        return customerDeviceRepository.saveAndFlush(device);
    }
}
