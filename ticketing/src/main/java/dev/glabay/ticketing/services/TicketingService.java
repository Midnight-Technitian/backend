package dev.glabay.ticketing.services;

import dev.glabay.dtos.ServiceTicketDto;
import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.events.ServiceTicketEvents;
import dev.glabay.models.ServiceTicketStatus;
import dev.glabay.models.request.ServiceRequest;
import dev.glabay.services.SequenceGeneratorService;
import dev.glabay.ticketing.models.ServiceTicket;
import dev.glabay.ticketing.repos.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-22
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TicketingService {
    private final Logger log = LoggerFactory.getLogger(TicketingService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final TicketRepository ticketRepository;

    public ServiceTicket createServiceTicket(ServiceRequest serviceRequest) {
        var ticket = new ServiceTicket();
        // Create a unique ID for Mongo
        if (ticket.getId() == null)
            ticket.setId(sequenceGeneratorService.getNextSequence("midnight_ticket_seq"));
        ticket.setTicketId(sequenceGeneratorService.getNextTicketSequence());
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
        log.info("Creating service ticket {}", ticket);
        saveTicket(ticket);

        var event = new ServiceTicketEvents.ServiceTicketCreatedEvent(ticket.mapToDto());
        kafkaTemplate.send(KafkaTopics.SERVICE_TICKET_CREATED.getTopicName(), ticket.getCustomerId(), event);
        return ticket;
    }

    public ServiceTicket updateServiceTicket(ServiceTicketDto dto) {
        var optionalServiceTicket = getServiceTicket(dto.getTicketId());
        if (optionalServiceTicket.isEmpty())
            return null;
        var ticket = optionalServiceTicket.get();
            ticket.setTicketId(dto.getTicketId());
            ticket.setStatus(dto.getStatus());
            ticket.setTitle(dto.getTitle());
            ticket.setDescription(dto.getDescription());
            ticket.setCreatedAt(dto.getCreatedAt());
            ticket.setUpdatedAt(LocalDateTime.now());
            ticket.setCustomerId(dto.getCustomerId());
            ticket.setCustomerDeviceId(dto.getCustomerDeviceId());
            ticket.setEmployeeId(dto.getEmployeeId());
            ticket.setServiceId(dto.getServiceId());
            ticket.setNotes(dto.getNotes());
        log.info("Updating service ticket {}", ticket);
        saveTicket(ticket);
        var event = new ServiceTicketEvents.ServiceTicketUpdatedEvent(ticket.mapToDto());
        kafkaTemplate.send(KafkaTopics.SERVICE_TICKET_UPDATED.getTopicName(), ticket.getCustomerId(), event);
        return ticket;
    }

    public ServiceTicket closeServiceTicket(ServiceTicketDto dto) {
        var optionalServiceTicket = getServiceTicket(dto.getTicketId());
        if (optionalServiceTicket.isEmpty())
            return null;
        var ticket = optionalServiceTicket.get();
            ticket.setTicketId(dto.getTicketId());
            ticket.setStatus(String.valueOf(ServiceTicketStatus.CLOSED));
            ticket.setTitle(dto.getTitle());
            ticket.setDescription(dto.getDescription());
            ticket.setCreatedAt(dto.getCreatedAt());
            ticket.setUpdatedAt(LocalDateTime.now());
            ticket.setCustomerId(dto.getCustomerId());
            ticket.setCustomerDeviceId(dto.getCustomerDeviceId());
            ticket.setEmployeeId(dto.getEmployeeId());
            ticket.setServiceId(dto.getServiceId());
            ticket.setNotes(dto.getNotes());
        log.info("Updating service ticket {}", ticket);
        saveTicket(ticket);
        var event = new ServiceTicketEvents.ServiceTicketUpdatedEvent(ticket.mapToDto());
        kafkaTemplate.send(KafkaTopics.SERVICE_TICKET_CLOSED.getTopicName(), ticket.getCustomerId(), event);
        return ticket;
    }

    public void saveTicket(ServiceTicket serviceTicket) {
        ticketRepository.save(serviceTicket);
    }

    public Long getTotalOpenTickets() {
        var openTickets = ticketRepository.findAll().stream()
            .filter(ticket -> !ServiceTicketStatus.CLOSED.getStatus().equals(ticket.getStatus()))
            .toList();
        return (long) openTickets.size();
    }

    public Long getTotalClosedTickets() {
        var closedTickets = ticketRepository.findAll().stream()
            .filter(ticket -> ServiceTicketStatus.CLOSED.getStatus().equals(ticket.getStatus()))
            .toList();
        return (long) closedTickets.size();
    }

    public List<ServiceTicketDto> findAllRecentTickets() {
        var sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return ticketRepository.findByCreatedAtAfter(sevenDaysAgo)
            .stream()
            .map(ServiceTicket::mapToDto)
            .toList();
    }

    public Optional<ServiceTicket> getServiceTicket(String ticketId) {
        log.info("Getting service ticket {}", ticketId);
        var serviceTicket = ticketRepository.findByTicketId(ticketId);
        if (serviceTicket.isPresent()) {
            log.info("Service ticket found {}", serviceTicket.get());
            return serviceTicket;
        }
        log.info("Service ticket not found {}", ticketId);
        return Optional.empty();
    }

    public List<ServiceTicketDto> getAllOpenTickets(String customerEmail, int page, int size) {
        if (size <= 0)
            size = 15;
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ticketRepository.findAllByCustomerId(customerEmail).stream()
            .filter(ticket -> !ServiceTicketStatus.CLOSED.getStatus().equalsIgnoreCase(ticket.getStatus()))
            .map(ServiceTicket::mapToDto)
            .toList();
    }

    public List<ServiceTicketDto> getAllOpenTickets(int page, int size) {
        if (size <= 0)
            size = 15;
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ticketRepository.findAll(pageable).stream()
            .filter(ticket -> !ServiceTicketStatus.CLOSED.getStatus().equalsIgnoreCase(ticket.getStatus()))
            .map(ServiceTicket::mapToDto)
            .toList();
    }

    public List<ServiceTicketDto> getAllOpenClaimedTickets(int page, int size) {
        var openTickets = getAllOpenTickets(page, size);
        return openTickets.stream()
            // check the employee is not blank or unassigned
            .filter(ticket -> !ticket.getEmployeeId().isBlank() &&
                !ticket.getEmployeeId().equalsIgnoreCase("Not-Assigned"))
            .toList();
    }

    public List<ServiceTicketDto> getAllOpenUnclaimedTickets(int page, int size) {
        if (size <= 0)
            size = 15;
        var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ticketRepository.findAll(pageable).stream()
            // check the status is not CLOSED
            .filter(ticket -> !ServiceTicketStatus.CLOSED.getStatus().equals(ticket.getStatus()))
            // check the employee is blank, or unassigned
            .filter(ticket -> ticket.getEmployeeId().isBlank() || ticket.getEmployeeId().equalsIgnoreCase("Not-Assigned"))
            // map to a DTO
            .map(ServiceTicket::mapToDto)
            // return the list
            .toList();
    }

}
