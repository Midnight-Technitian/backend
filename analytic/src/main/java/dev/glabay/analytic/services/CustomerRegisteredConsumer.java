package dev.glabay.analytic.services;

import dev.glabay.kafka.events.customer.CustomerRegisteredEvent;
import dev.glabay.logging.MidnightLogger;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-11-11
 */
@Service
@NullMarked
public class CustomerRegisteredConsumer {
    private final Logger logger = new MidnightLogger(CustomerRegisteredConsumer.class);

    @KafkaListener(topics = "customer-created", groupId = "customer-service")
    public void handleCustomerRegistered(CustomerRegisteredEvent event) {
        logger.info("Received event: {}", event);
        // TODO: log information about customer creation
    }
}
