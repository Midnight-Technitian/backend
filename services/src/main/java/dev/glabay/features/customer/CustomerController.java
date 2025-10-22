package dev.glabay.features.customer;

import dev.glabay.dtos.CustomerDto;
import dev.glabay.dtos.UserProfileDto;
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
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    private ResponseEntity<CustomerDto> createCustomer(@RequestBody UserProfileDto dto) {
        return ResponseEntity.ok(customerService.createCustomer(dto));
    }

    @GetMapping("/{customerId}")
    private ResponseEntity<CustomerDto> getCustomerById(@PathVariable String customerId) {
        var custId = Integer.parseInt(customerId);
        return ResponseEntity.ok(customerService.getCustomerById(custId));
    }

    @GetMapping("/e/{email}")
    private ResponseEntity<CustomerDto> getCustomerByEmail(@PathVariable String email) {
        var customer = customerService.getCustomerByEmail(email);
        if (customer == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

}
