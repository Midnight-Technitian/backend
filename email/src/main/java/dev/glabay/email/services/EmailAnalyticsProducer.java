package dev.glabay.email.services;

import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.metric.EmailSentAnalyticEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailAnalyticsProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EmailAnalyticsProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String key, EmailSentAnalyticEvent event) {
        kafkaTemplate.send(KafkaTopics.EMAIL_SENT_ANALYTIC.getTopicName(), key, event);
    }

    public void publishPending(String emailId, String recipient, String templateName, String triggeredBy, String serviceOrigin) {
        publish(emailId, new EmailSentAnalyticEvent(
            emailId,
            recipient,
            templateName,
            triggeredBy,
            LocalDateTime.now(),
            dev.glabay.kafka.metric.EmailStatus.PENDING,
            serviceOrigin
        ));
    }

    public void publishSent(String emailId, String recipient, String templateName, String triggeredBy, String serviceOrigin) {
        publish(emailId, new EmailSentAnalyticEvent(
            emailId,
            recipient,
            templateName,
            triggeredBy,
            LocalDateTime.now(),
            dev.glabay.kafka.metric.EmailStatus.SENT,
            serviceOrigin
        ));
    }

    public void publishFailed(String emailId, String recipient, String templateName, String triggeredBy, String serviceOrigin) {
        publish(emailId, new EmailSentAnalyticEvent(
            emailId,
            recipient,
            templateName,
            triggeredBy,
            LocalDateTime.now(),
            dev.glabay.kafka.metric.EmailStatus.FAILED,
            serviceOrigin
        ));
    }
}
