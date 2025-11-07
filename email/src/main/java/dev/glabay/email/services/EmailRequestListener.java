package dev.glabay.email.services;

import dev.glabay.kafka.email.EmailSendRequest;
import dev.glabay.logging.MidnightLogger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class EmailRequestListener {

    private final Logger log = MidnightLogger.getLogger(EmailRequestListener.class);

    private final EmailTemplateRenderer renderer;
    private final EmailProvider emailProvider;
    private final EmailAnalyticsProducer analyticsProducer;
    private final IdempotencyService idempotencyService;

    public EmailRequestListener(
        EmailTemplateRenderer renderer,
        EmailProvider emailProvider,
        EmailAnalyticsProducer analyticsProducer,
        IdempotencyService idempotencyService
    ) {
        this.renderer = renderer;
        this.emailProvider = emailProvider;
        this.analyticsProducer = analyticsProducer;
        this.idempotencyService = idempotencyService;
    }

    @KafkaListener(topics = "email-send-request", groupId = "midnight-email-service")
    public void handleEmailRequest(EmailSendRequest request,
                                   @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                                   ConsumerRecord<String, Object> record) {
        String emailId = request.emailId();
        if (emailId == null || emailId.isBlank()) {
            // fallback to key if present
            emailId = key != null ? key : "unknown";
        }

        if (idempotencyService.isProcessed(emailId)) {
            log.info("Duplicate email detected, skipping. emailId={}", emailId);
            return;
        }

        log.info("Processing email request: id={}, recipient={}, template={} from={}", emailId, request.recipient(), request.templateName(), request.serviceOrigin());
        analyticsProducer.publishPending(emailId, request.recipient(), request.templateName(), request.triggeredBy(), request.serviceOrigin());

        try {
            String html = renderer.render(request.templateName(), request.templateData());
            String messageId = emailProvider.send(request.recipient(), request.subject(), html, request.replyTo());
            log.info("Email sent via provider. emailId={}, providerMessageId={}", emailId, messageId);
            idempotencyService.markProcessed(emailId);
            analyticsProducer.publishSent(emailId, request.recipient(), request.templateName(), request.triggeredBy(), request.serviceOrigin());
        }
        catch (Exception ex) {
            log.error("Email send failed. emailId={}, recipient={}, template={}, error={}", emailId, request.recipient(), request.templateName(), ex);
            analyticsProducer.publishFailed(emailId, request.recipient(), request.templateName(), request.triggeredBy(), request.serviceOrigin());
            throw ex; // allow retry/backoff and DLT
        }
    }
}
