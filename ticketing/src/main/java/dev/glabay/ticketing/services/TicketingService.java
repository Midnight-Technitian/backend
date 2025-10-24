package dev.glabay.ticketing.services;

import dev.glabay.models.ServiceTicketStatus;
import dev.glabay.models.request.ServiceRequest;
import dev.glabay.services.SequenceGeneratorService;
import dev.glabay.ticketing.models.ServiceNote;
import dev.glabay.ticketing.models.ServiceTicket;
import dev.glabay.ticketing.repos.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-22
 */
@Service
@RequiredArgsConstructor
public class TicketingService {
    private final SequenceGeneratorService sequenceGeneratorService;
    private final TicketRepository ticketRepository;

    public ServiceTicket createServiceTicket(ServiceRequest serviceRequest) {
        var ticket = new ServiceTicket();
        // Create a unique ID for Mongo
        if (ticket.getTicketId() == null)
            ticket.setTicketId(sequenceGeneratorService.getNextSequence("service_ticket_seq"));

        ticket.setStatus(ServiceTicketStatus.PENDING.getStatus());
        ticket.setTitle(serviceRequest.deviceName());
        ticket.setDescription(serviceRequest.serviceDescription());
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticket.setCustomerId(serviceRequest.customerEmail());
        ticket.setCustomerDeviceId(serviceRequest.deviceId());
        ticket.setEmployeeId("Not-Assigned");
        ticket.setServiceId(serviceRequest.serviceId());
        ticket.setNotes(new ArrayList<>());

        saveTicket(ticket);
        return ticket;
    }

    public void saveTicket(ServiceTicket serviceTicket) {
        ticketRepository.save(serviceTicket);
    }
}
