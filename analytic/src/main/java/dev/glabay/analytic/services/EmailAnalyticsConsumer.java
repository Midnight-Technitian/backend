package dev.glabay.analytic.services;

import dev.glabay.analytic.models.EmailActivityFactEntity;
import dev.glabay.analytic.models.EmailAnalyticEntity;
import dev.glabay.analytic.repos.EmailActivityFactEntityRepository;
import dev.glabay.analytic.repos.EmailAnalyticEntityRepository;
import dev.glabay.kafka.metric.EmailSentAnalyticEvent;
import dev.glabay.kafka.metric.EmailStatus;
import dev.glabay.logging.MidnightLogger;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;

/**
 * Consumes email analytics events and persists them into dim/fact tables.
 */
@Service
@NullMarked
public class EmailAnalyticsConsumer {
    private final Logger logger = new MidnightLogger(EmailAnalyticsConsumer.class);

    private final EmailAnalyticEntityRepository templateRepo;
    private final EmailActivityFactEntityRepository factRepo;

    public EmailAnalyticsConsumer(EmailAnalyticEntityRepository templateRepo,
                                  EmailActivityFactEntityRepository factRepo) {
        this.templateRepo = templateRepo;
        this.factRepo = factRepo;
    }

    @KafkaListener(topics = "email-sent-analytic", groupId = "analytic-service")
    public void handleEmailAnalytics(EmailSentAnalyticEvent event) {
        logger.info("Email analytics event: id={}, recipient={}, template={}, status={}, at={}, origin={}",
            event.emailId(), event.recipient(), event.templateName(), event.status(), event.sentAt(), event.serviceOrigin());

        // 1) Ensure template dimension exists (upsert by template_name, case-insensitive)
        String templateKey = event.templateName() != null ? event.templateName().toLowerCase(Locale.ROOT) : "unknown";
        EmailAnalyticEntity template = templateRepo.findByTemplateNameIgnoreCase(templateKey)
            .orElseGet(() -> {
                var t = new EmailAnalyticEntity();
                t.setTemplateName(event.templateName() != null ? event.templateName() : "unknown");
                t.setDescription(null);
                return templateRepo.save(t);
            });

        // 2) Upsert fact by emailId
        var existingOpt = factRepo.findByEmailId(event.emailId());
        if (existingOpt.isEmpty()) {
            // First time seeing this emailId
            var fact = new EmailActivityFactEntity();
            fact.setEmailId(event.emailId());
            fact.setRecipient(event.recipient());
            fact.setTemplate(template);
            fact.setTriggeredBy(event.triggeredBy());
            fact.setServiceOrigin(event.serviceOrigin());
            fact.setSentAt(event.sentAt());
            fact.setStatus(event.status().name());
            fact.setLatencyMs(null);
            var saved = factRepo.save(fact);
            logger.info("Inserted email fact: id={}, emailId={}, templateId={}, status={}", saved.getId(), saved.getEmailId(), template.getId(), saved.getStatus());
            return;
        }

        // Existing record
        var fact = existingOpt.get();
        var prevStatus = fact.getStatus();
        var newStatus = event.status();

        // Always keep referenced template consistent (in case templateId changed due to rename)
        fact.setTemplate(template);
        fact.setRecipient(event.recipient());
        fact.setTriggeredBy(event.triggeredBy());
        fact.setServiceOrigin(event.serviceOrigin());

        if (newStatus == EmailStatus.PENDING) {
            // Keep the earliest sentAt for PENDING to compute latency basis
            if (fact.getSentAt() == null || event.sentAt().isBefore(fact.getSentAt())) {
                fact.setSentAt(event.sentAt());
            }
            fact.setStatus(EmailStatus.PENDING.name());
            factRepo.save(fact);
            logger.info("Updated email fact as PENDING (emailId={}, basisAt={})", fact.getEmailId(), fact.getSentAt());
            return;
        }

        // Final state (SENT or FAILED)
        if (prevStatus != null && prevStatus.equals(EmailStatus.PENDING.name()) && fact.getSentAt() != null) {
            long latency = Duration.between(fact.getSentAt(), event.sentAt()).toMillis();
            fact.setLatencyMs(Math.max(latency, 0));
        }
        // Update sentAt to the final timestamp and status
        fact.setSentAt(event.sentAt());
        fact.setStatus(newStatus.name());

        var saved = factRepo.save(fact);
        logger.info("Finalized email fact (emailId={}, status={}, latencyMs={})", saved.getEmailId(), saved.getStatus(), saved.getLatencyMs());
    }
}
