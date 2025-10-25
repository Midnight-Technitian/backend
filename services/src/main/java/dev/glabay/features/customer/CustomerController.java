package dev.glabay.features.customer;

import dev.glabay.dtos.CustomerDto;
import dev.glabay.dtos.UserProfileDto;
import dev.glabay.logging.MidnightLogger;
import org.slf4j.Logger;
import org.springframework.data.repository.query.Param;
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
    private final Logger logger = MidnightLogger.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    private ResponseEntity<CustomerDto> createCustomer(@RequestBody UserProfileDto dto) {
        logger.info("New user is registering: {}", dto);
        return ResponseEntity.ok(customerService.createCustomer(dto));
    }

    @GetMapping("/{customerId}")
    private ResponseEntity<CustomerDto> getCustomerById(@PathVariable String customerId) {
        var custId = Long.parseLong(customerId);
        logger.info("Get customer by id: {}", customerId);
        return ResponseEntity.ok(customerService.getCustomerById(custId));
    }

    @GetMapping("/email")
    private ResponseEntity<CustomerDto> getCustomerByEmail(@Param("email") String email) {
        var customer = customerService.getCustomerByEmail(email);
        logger.info("Get customer by email: {}", email);
        if (customer == null) {
            logger.info("No customer found for email: {}", email);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

}
