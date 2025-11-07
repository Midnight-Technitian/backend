package dev.glabay.analytic.services;

import dev.glabay.kafka.metric.EmailSentAnalyticEvent;
import dev.glabay.logging.MidnightLogger;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consumes email analytics events for dashboards and auditing.
 */
@Service
@NullMarked
public class EmailAnalyticsConsumer {
    private final Logger logger = new MidnightLogger(EmailAnalyticsConsumer.class);

    @KafkaListener(topics = "email-sent-analytic", groupId = "analytic-service")
    public void handleEmailAnalytics(EmailSentAnalyticEvent event) {
        // For v1, just log the analytics event. Persistence/aggregation can be added later.
        logger.info("Email analytics event: id={}, recipient={}, template={}, status={}, at={}, origin={}",
            event.emailId(), event.recipient(), event.templateName(), event.status(), event.sentAt(), event.serviceOrigin());
    }
}
