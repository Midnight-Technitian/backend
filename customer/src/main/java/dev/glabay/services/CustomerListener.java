package dev.glabay.services;

import dev.glabay.customer.services.CustomerService;
import dev.glabay.kafka.events.customer.CustomerRegisteredEvent;
import dev.glabay.kafka.events.user.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-11-01
 */
@Service
@NullMarked
@RequiredArgsConstructor
public class CustomerListener {
    private final CustomerService customerService;

    @KafkaListener(topics = "user-registered", groupId = "customer-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        System.out.println("Received event: " + event);
        var userDto = event.userDto();
        customerService.createCustomer(userDto);
    }

    @KafkaListener(topics = "customer-created", groupId = "customer-service")
    public void handleCustomerRegistered(CustomerRegisteredEvent event) {
        System.out.println("Received event: " + event);
        customerService.createCustomer(
            event.customerEmail(),
            event.firstName(),
            event.lastName(),
            event.contactNumber()
        );
    }
}

