package dev.glabay.services;

import dev.glabay.kafka.ServiceTicketCreationEvent;
import dev.glabay.logging.MidnightLogger;
import dev.glabay.ticketing.services.TicketingService;
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
public class TicketListener {
    private final Logger logger = MidnightLogger.getLogger(TicketListener.class);
    private final TicketingService ticketingService;

    @KafkaListener(topics = "service-ticket-creation", groupId = "ticketing-service")
    public void handleTicketCreation(ServiceTicketCreationEvent event) {
        logger.info("Received event: {}", event);
        var request = event.requestDto();
        var cached = ticketingService.createServiceTicket(request);
        logger.info("Created ticket: {}", cached);
    }
}
