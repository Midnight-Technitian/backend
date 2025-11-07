package dev.glabay.ticketing.email;

import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.email.EmailSendRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class TicketEmailPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TicketEmailPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTicketCreatedEmail(
        String ticketId,
        String recipientEmail,
        String customerName,
        String summary,
        LocalDateTime createdAt,
        String triggeredBy
    ) {
        String emailId = "email-" + ticketId + "-" + UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();

        var request = new EmailSendRequest(
            emailId,
            recipientEmail,
            "Ticket #" + ticketId + " created",
            "ticket-created",
            Map.of(
                "ticketId", ticketId,
                "customerName", customerName,
                "summary", summary,
                // Use string to avoid LocalDateTime serialization differences across modules
                "createdAt", createdAt != null ? createdAt.toString() : LocalDateTime.now().toString()
            ),
            triggeredBy,
            "ticketing",
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
