package dev.glabay.customer.services;

import dev.glabay.customer.models.Customer;
import dev.glabay.customer.repos.CustomerRepository;
import dev.glabay.dtos.CustomerDto;
import dev.glabay.dtos.UserProfileDto;
import dev.glabay.services.CustomerConverter;
import dev.glabay.services.SequenceGeneratorService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
@Service
public class CustomerService implements CustomerConverter {
    private final CustomerRepository customerRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public CustomerService(CustomerRepository customerRepository, SequenceGeneratorService sequenceGeneratorService) {
        this.customerRepository = customerRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    public CustomerDto getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
            .map(this::mapToDto)
            .orElse(null);
    }

    public CustomerDto getCustomerByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email)
            .map(this::mapToDto)
            .orElse(null);
    }

    public Long getCustomerIdByEmail(String email) {
        var customerId = new AtomicLong(-1L);
        customerRepository.findByEmailIgnoreCase(email)
            .ifPresent(customer -> customerId.set(customer.getCustomerId()));
        return customerId.get();
    }

    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .toList();
    }

    public CustomerDto createCustomer(UserProfileDto dto) {
        var customer = new Customer();
        if (customer.getCustomerId() == null)
            customer.setCustomerId(sequenceGeneratorService.getNextCustomerSequence("midnight_customer_sequences"));
        customer.setContactNumber(dto.contactNumber());
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setEmail(dto.email());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        // save the Customer
        customerRepository.save(customer);
        return mapToDto(customer);
    }
}
