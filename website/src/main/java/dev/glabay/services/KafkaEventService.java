package dev.glabay.services;

import dev.glabay.dtos.UserProfileDto;
import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.events.customer.CustomerRegisteredEvent;
import dev.glabay.kafka.events.device.CustomerDeviceRegistrationEvent;
import dev.glabay.kafka.events.ticket.ServiceTicketCreationEvent;
import dev.glabay.kafka.events.user.UserRegisteredEvent;
import dev.glabay.logging.MidnightLogger;
import dev.glabay.models.device.RegisteringDevice;
import dev.glabay.models.request.ServiceRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
@Service
@RequiredArgsConstructor
public class KafkaEventService {
    private final Logger logger = MidnightLogger.getLogger(KafkaEventService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistration(UserProfileDto dto, String ipAddress) {
        var event = new UserRegisteredEvent(dto, ipAddress);
        kafkaTemplate.send(KafkaTopics.USER_REGISTRATION.getTopicName(), dto.email(), event);
        logger.info("User Registered Event sent to Kafka {}", event);
    }

    public void publishCustomerRegistration(String email, String firstName, String lastName, String contactNumber,  String employeeId) {
        var event = new CustomerRegisteredEvent(email, firstName, lastName, contactNumber, employeeId);
        kafkaTemplate.send(KafkaTopics.CUSTOMER_CREATION.getTopicName(), email, event);
        logger.info("Customer Registered Event sent to Kafka {}", event);
    }

    public void publishDeviceRegistration(RegisteringDevice body) {
        var event = new CustomerDeviceRegistrationEvent(body);
        kafkaTemplate.send(KafkaTopics.CUSTOMER_DEVICE_REGISTRATION.getTopicName(), body.getCustomerEmail(), event);
        logger.info("New Customer Device Registration Event sent to Kafka {}", event);
    }

    public void publishServiceTicketRequest(ServiceRequest body) {
        var event = new ServiceTicketCreationEvent(body);
        kafkaTemplate.send(KafkaTopics.SERVICE_TICKET_CREATION.getTopicName(), body.customerEmail(), event);
        logger.info("New Service Ticket Creation Event sent to Kafka {}", event);
    }
}
