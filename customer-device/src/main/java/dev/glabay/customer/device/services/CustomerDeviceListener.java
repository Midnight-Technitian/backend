package dev.glabay.customer.device.services;

import dev.glabay.kafka.events.device.CustomerDeviceRegistrationEvent;
import dev.glabay.logging.MidnightLogger;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-11-02
 */
@Service
@NullMarked
@RequiredArgsConstructor
public class CustomerDeviceListener {
    private final Logger logger = MidnightLogger.getLogger(CustomerDeviceListener.class);
    private final CustomerDeviceService customerDeviceService;

    @KafkaListener(topics = "customer-device-registration", groupId = "customer-device-service")
    public void handleDeviceRegistration(CustomerDeviceRegistrationEvent event) {
        logger.info("Handling device registration event: {}", event);
        var cachedDevice = customerDeviceService.createNewCustomerDevice(event.registeringDevice());
        logger.info("Created device: {}", cachedDevice);
    }
}
