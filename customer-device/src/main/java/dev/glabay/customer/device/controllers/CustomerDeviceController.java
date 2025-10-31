package dev.glabay.customer.device.controllers;

import dev.glabay.customer.device.services.CustomerDeviceService;
import dev.glabay.dtos.CustomerDeviceDto;
import dev.glabay.models.device.RegisteringDevice;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
@NullMarked
@RestController
@RequestMapping("/api/v1/devices")
public class CustomerDeviceController {
    private final CustomerDeviceService customerDeviceService;

    public CustomerDeviceController(CustomerDeviceService customerDeviceService) {
        this.customerDeviceService = customerDeviceService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerDeviceDto>> getAllCustomerDevices() {
        var devices = customerDeviceService.getAllCustomerDevices();
        if (devices.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(devices);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDeviceDto>> getCustomerDevices(@RequestParam("email") String email) {
        var devices = customerDeviceService.getCustomerDevices(email);
        if (devices.isEmpty())
            return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/device")
    public ResponseEntity<CustomerDeviceDto> getCustomerDeviceByID(@RequestParam("deviceId") Long deviceId) {
        var device = customerDeviceService.getCustomerDeviceById(deviceId);
        if (device == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(device);
    }

    @PostMapping
    private ResponseEntity<CustomerDeviceDto> createCustomerDevice(@RequestBody RegisteringDevice dto) {
        var deviceDto = customerDeviceService.createNewCustomerDevice(dto);
        if (deviceDto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(deviceDto);
    }
}
