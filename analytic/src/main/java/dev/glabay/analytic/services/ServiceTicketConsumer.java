package dev.glabay.analytic.services;

import dev.glabay.analytic.models.ServiceTicketEntity;
import dev.glabay.analytic.repos.ServiceTicketEntityRepository;
import dev.glabay.kafka.events.ServiceTicketEvents;
import dev.glabay.logging.MidnightLogger;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-11-03
 */
@Service
@NullMarked
public class ServiceTicketConsumer {
    private final Logger logger = new MidnightLogger(ServiceTicketConsumer.class);

    private final ServiceTicketEntityRepository serviceTicketEntityRepository;

    public ServiceTicketConsumer(ServiceTicketEntityRepository serviceTicketEntityRepository) {
        this.serviceTicketEntityRepository = serviceTicketEntityRepository;
    }

    @KafkaListener(topics = "service-ticket-created", groupId = "analytic-service")
    public void handleCreatingNewTicket(ServiceTicketEvents.ServiceTicketCreatedEvent event) {
        logger.info("Received event: {}", event);
        var request = event.requestDto();
        var ticketDim = new ServiceTicketEntity();
            ticketDim.setTicketId(request.getTicketId());
            ticketDim.setServiceId(request.getServiceId());
            ticketDim.setDeviceId(request.getCustomerDeviceId());
            ticketDim.setEmployeeId(request.getEmployeeId());
            ticketDim.setCreatedAt(LocalDateTime.now());
            ticketDim.setClosedAt(null);
        if (!request.getEmployeeId().isBlank() && !request.getEmployeeId().equals("Not-Assigned"))
            ticketDim.setClaimedAt(LocalDateTime.now());

        var cachedRecord = serviceTicketEntityRepository.save(ticketDim);
        logger.info("Saved service ticket entity: {}", cachedRecord);
    }

    @KafkaListener(topics = "service-ticket-update", groupId = "analytic-service")
    public void handleUpdatingServiceTicket(ServiceTicketEvents.ServiceTicketUpdatedEvent event) {
        logger.info("Received event: {}", event);
        var request = event.requestDto();
        var optionalTicketDim = serviceTicketEntityRepository.findByTicketId(request.getTicketId());
        if (optionalTicketDim.isEmpty()) {
            logger.error("Service ticket not found with id {}", request.getTicketId());
            return;
        }
        var ticketDim = optionalTicketDim.get();
            ticketDim.setServiceId(request.getServiceId());
            ticketDim.setDeviceId(request.getCustomerDeviceId());
            ticketDim.setEmployeeId(request.getEmployeeId());
            ticketDim.setCreatedAt(LocalDateTime.now());
        if (!request.getEmployeeId().isBlank() && !request.getEmployeeId().equals("Not-Assigned"))
            ticketDim.setClaimedAt(LocalDateTime.now());

        var cachedRecord = serviceTicketEntityRepository.save(ticketDim);
        logger.info("Saved service ticket entity: {}", cachedRecord);
    }

    @KafkaListener(topics = "service-ticket-claimed", groupId = "analytic-service")
    public void handleClaimingServiceTicket(ServiceTicketEvents.ServiceTicketClaimedEvent event) {
        logger.info("Received event: {}", event);
        var request = event.requestDto();
        var optionalTicketDim = serviceTicketEntityRepository.findByTicketId(request.getTicketId());
        if (optionalTicketDim.isEmpty()) {
            logger.error("Service ticket not found with id {}", request.getTicketId());
            return;
        }
        var ticketDim = optionalTicketDim.get();
            ticketDim.setServiceId(request.getServiceId());
            ticketDim.setDeviceId(request.getCustomerDeviceId());
            ticketDim.setEmployeeId(request.getEmployeeId());
            ticketDim.setCreatedAt(request.getCreatedAt());
            ticketDim.setClaimedAt(LocalDateTime.now());

        var cachedRecord = serviceTicketEntityRepository.save(ticketDim);
        logger.info("Saved service ticket entity: {}", cachedRecord);
    }

    @KafkaListener(topics = "service-ticket-closed", groupId = "analytic-service")
    public void handleClosingServiceTicket(ServiceTicketEvents.ServiceTicketUpdatedEvent event) {
        logger.info("Received event: {}", event);
        var request = event.requestDto();
        var optionalTicketDim = serviceTicketEntityRepository.findByTicketId(request.getTicketId());
        if (optionalTicketDim.isEmpty()) {
            logger.error("Service ticket not found with id {}", request.getTicketId());
            return;
        }
        var ticketDim = optionalTicketDim.get();
            ticketDim.setClosedAt(LocalDateTime.now());

        var cachedRecord = serviceTicketEntityRepository.save(ticketDim);
        logger.info("Saved service ticket entity: {}", cachedRecord);
    }
}
