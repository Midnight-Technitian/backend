package dev.glabay.features.customer;

import dev.glabay.dtos.CustomerDto;
import dev.glabay.dtos.UserProfileDto;
import dev.glabay.inter.impl.CustomerConverter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
@Service
public class CustomerService implements CustomerConverter {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerDto getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId)
            .map(this::mapToDto)
            .orElse(null);
    }

    public CustomerDto getCustomerByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email)
            .map(this::mapToDto)
            .orElse(null);
    }

    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .toList();
    }

    public CustomerDto createCustomer(UserProfileDto dto) {
        var customer = new Customer();
            customer.setContactNumber(dto.contactNumber());
            customer.setFirstName(dto.firstName());
            customer.setLastName(dto.lastName());
            customer.setEmail(dto.email());
        // save the Customer
        customerRepository.saveAndFlush(customer);
        return mapToDto(customer);
    }
}
