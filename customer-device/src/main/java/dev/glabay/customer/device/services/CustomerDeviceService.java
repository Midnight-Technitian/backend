package dev.glabay.customer.device.services;

import dev.glabay.customer.device.models.CustomerDevice;
import dev.glabay.customer.device.repos.CustomerDeviceRepository;
import dev.glabay.dtos.CustomerDeviceDto;
import dev.glabay.customer.device.CustomerDeviceConverter;
import dev.glabay.models.device.RegisteringDevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDeviceService implements CustomerDeviceConverter {
    private final SequenceGeneratorService sequenceGeneratorService;
    private final CustomerDeviceRepository customerDeviceRepository;

    public CustomerDeviceDto getCustomerDeviceById(Long deviceId) {
        return customerDeviceRepository.findById(deviceId)
                .map(this::mapToDto)
                .orElseThrow(() -> new IllegalArgumentException("Device not found with ID: " + deviceId));
    }

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
        if (device.getDeviceId() == null)
            device.setDeviceId(sequenceGeneratorService.getNextCustomerSequence("midnight_customer_device_sequences"));
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
        var cached = customerDeviceRepository.save(device);
        log.info("New device registered: {}", cached);
        return cached;
    }
}
