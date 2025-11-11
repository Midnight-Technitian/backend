package dev.glabay.services;

import dev.glabay.dtos.CustomerDto;
import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.email.EmailSendRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCustomerCreatedAnalyticEvent(CustomerDto dto, String employeeId) {
        kafkaTemplate.send(KafkaTopics.CUSTOMER_CREATED_ANALYTIC.getTopicName(), dto);
    }

    public void publishWelcomeEmailEvent(String customerId, String recipientEmail, String customerName, String triggeredBy) {
        String emailId = "email-" + customerId + "-" + UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();

        var request = new EmailSendRequest(
            emailId,
            recipientEmail,
            "Welcome to Midnight Technician",
            "welcome-customer",
            Map.of("customerName", customerName != null && !customerName.isBlank() ? customerName : "Customer"),
            triggeredBy,
            "customer",
            correlationId,
            null
        );

        kafkaTemplate.send(
            KafkaTopics.EMAIL_SEND_REQUEST.getTopicName(),
            request.emailId(),
            request
        );
    }
}
