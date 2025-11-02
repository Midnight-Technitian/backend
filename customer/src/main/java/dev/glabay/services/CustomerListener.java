package dev.glabay.services;

import dev.glabay.customer.services.CustomerService;
import dev.glabay.kafka.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CustomerService customerService;

    @KafkaListener(topics = "user-registered", groupId = "customer-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        System.out.println("Received event: " + event);
        var userDto = event.userDto();
        customerService.createCustomer(userDto);
        // TODO: produce another event for the email service
//        var customerCreated = new CustomerCreatedEvent(user.email(), user.firstName(), user.lastName());
//        kafkaTemplate.send(KafkaTopics.CUSTOMER_CREATION.getTopicName(), user.email(), customerCreated);
    }
}

