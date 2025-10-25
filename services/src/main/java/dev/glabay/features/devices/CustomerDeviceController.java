package dev.glabay.features.devices;

import dev.glabay.dtos.CustomerDeviceDto;
import dev.glabay.features.customer.CustomerService;
import dev.glabay.models.device.RegisteringDevice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
@RestController
@RequestMapping("/api/v1/devices")
public class CustomerDeviceController {
    private final CustomerService customerService;
    private final CustomerDeviceService customerDeviceService;

    public CustomerDeviceController(CustomerService customerService, CustomerDeviceService customerDeviceService) {
        this.customerService = customerService;
        this.customerDeviceService = customerDeviceService;
    }

    @GetMapping("/all")
    public List<CustomerDeviceDto> getAllCustomerDevices() {
        return customerDeviceService.getAllCustomerDevices();
    }

    @GetMapping
    public List<CustomerDeviceDto> getCustomerDevices(@RequestParam("email") String email) {
        return customerDeviceService.getCustomerDevices(email);
    }

    @PostMapping
    private ResponseEntity<CustomerDeviceDto> createCustomerDevice(@RequestBody RegisteringDevice dto) {
        var deviceDto = customerDeviceService.createNewCustomerDevice(dto);
        if (deviceDto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(deviceDto);
    }
}
