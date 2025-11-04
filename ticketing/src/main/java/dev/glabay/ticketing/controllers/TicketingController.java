package dev.glabay.ticketing.controllers;

import dev.glabay.dtos.ServiceTicketDto;
import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.events.ServiceTicketEvents;
import dev.glabay.logging.MidnightLogger;
import dev.glabay.models.ServiceNote;
import dev.glabay.models.ServiceTicketStatus;
import dev.glabay.models.request.ServiceRequest;
import dev.glabay.ticketing.services.TicketingService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-22
 */
@NullMarked
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketingController {
    private final Logger logger = MidnightLogger.getLogger(TicketingController.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TicketingService ticketingService;

    @PostMapping()
    private ResponseEntity<ServiceTicketDto> postNewServiceTicket(@RequestBody ServiceRequest requestDto) {
        if (requestDto.serviceDescription() == null || requestDto.serviceDescription().isBlank()) {
            logger.error("Service description is blank");
            return ResponseEntity.badRequest().build();
        }
        if (requestDto.customerEmail() == null || requestDto.customerEmail().isBlank()) {
            logger.error("Customer email is blank");
            return ResponseEntity.badRequest().build();
        }

        var ticket = ticketingService.createServiceTicket(requestDto);
        logger.info("Created service ticket with id {}", ticket.getId());
        return new ResponseEntity<>(ticket.mapToDto(), HttpStatus.CREATED);
    }

    @PutMapping()
    private ResponseEntity<ServiceTicketDto> updateServiceTicket(@RequestBody ServiceTicketDto dto) {
        var optionalTicket = ticketingService.getServiceTicket(dto.getTicketId());
        if (optionalTicket.isEmpty()) {
            logger.error("Service ticket not found with id {}", dto.getTicketId());
            return ResponseEntity.notFound().build();
        }
        var ticket = ticketingService.updateServiceTicket(dto);
        return ResponseEntity.ok(ticket.mapToDto());
    }

    @PutMapping("/claim")
    private ResponseEntity<Void> claimServiceTicket(
        @RequestParam("ticketId") String ticketId,
        @RequestParam("employeeId") String employeeId
    ) {
        var optionalTicket = ticketingService.getServiceTicket(ticketId);
        if (optionalTicket.isEmpty()) {
            logger.error("Service ticket not found with id {}", ticketId);
            return ResponseEntity.notFound().build();
        }
        var ticket = optionalTicket.get();
        ticket.setEmployeeId(employeeId);
        ticket.setStatus(String.valueOf(ServiceTicketStatus.OPEN));
        ticketingService.saveTicket(ticket);
        var event = new ServiceTicketEvents.ServiceTicketClaimedEvent(ticket.mapToDto());
        kafkaTemplate.send(KafkaTopics.SERVICE_TICKET_CLAIMED.getTopicName(), event);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/close")
    private ResponseEntity<Void> closeServiceTicket(@RequestParam("ticketId") String ticketId) {
        var optionalTicket = ticketingService.getServiceTicket(ticketId);
        if (optionalTicket.isEmpty()) {
            logger.error("Service ticket not found with id {}", ticketId);
            return ResponseEntity.notFound().build();
        }
        var cachedTicket = optionalTicket.get();
        cachedTicket.setStatus(String.valueOf(ServiceTicketStatus.CLOSED));
        ticketingService.closeServiceTicket(cachedTicket.mapToDto());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/note")
    private ResponseEntity<ServiceTicketDto> addNoteToTicket(@RequestBody ServiceNote noteDto) {
        var ticketId = noteDto.ticketId();
        var optionalTicket = ticketingService.getServiceTicket(ticketId);
        if (optionalTicket.isPresent()) {
            var serviceTicket = optionalTicket.get();
                serviceTicket.getNotes().add(noteDto);
            ticketingService.saveTicket(serviceTicket);
            return new ResponseEntity<>(serviceTicket.mapToDto(), HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    private ResponseEntity<ServiceTicketDto> getServiceTicket(@RequestParam("ticketId") String ticketId) {
        var optionalTicket = ticketingService.getServiceTicket(ticketId);
        return optionalTicket.map(serviceTicket ->
                new ResponseEntity<>(serviceTicket.mapToDto(), HttpStatus.OK))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/customer")
    private ResponseEntity<List<ServiceTicketDto>> getOpenTicketsForEmail(
        @RequestParam("email") String email,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size
    ) {
        var openTickets = ticketingService.getAllOpenTickets(email, page, size);
        if (openTickets.isEmpty()) {
            logger.error("No open tickets found for email {}", email);
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
        logger.info("Found {} open tickets for email {}", openTickets.size(), email);
        return new ResponseEntity<>(openTickets, HttpStatus.OK);
    }

    @GetMapping("/customer/history")
    private ResponseEntity<List<ServiceTicketDto>> getAllTicketsForEmail(
        @RequestParam("email") String email,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size
    ) {
        var ticketHistoryForEmail = ticketingService.getTicketHistoryForEmail(email, page, size);
        if (ticketHistoryForEmail.isEmpty()) {
            logger.error("No tickets found for email {}", email);
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
        logger.info("Found {} tickets for email {}", ticketHistoryForEmail.size(), email);
        return new ResponseEntity<>(ticketHistoryForEmail, HttpStatus.OK);
    }


    @GetMapping("/customer/device")
    private ResponseEntity<List<ServiceTicketDto>> getAllTicketsForDevice(
        @RequestParam("deviceId") String deviceId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size
    ) {
        var ticketHistoryForEmail = ticketingService.getTicketHistoryForDevice(deviceId, page, size);
        if (ticketHistoryForEmail.isEmpty()) {
            logger.error("No tickets found for deviceId {}", deviceId);
            return new ResponseEntity<>(List.of(), HttpStatus.OK);
        }
        logger.info("Found {} tickets for deviceId {}", ticketHistoryForEmail.size(), deviceId);
        return new ResponseEntity<>(ticketHistoryForEmail, HttpStatus.OK);
    }

    @GetMapping("/open")
    private ResponseEntity<List<ServiceTicketDto>> getOpenTickets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size
    ) {
        var openTickets = ticketingService.getAllOpenTickets(page, size);

        if (openTickets.isEmpty()) {
            logger.error("No open tickets found");
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(openTickets, HttpStatus.OK);
    }

    @GetMapping("/claimed")
    private ResponseEntity<List<ServiceTicketDto>> getOpenClaimedTickets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size
    ) {
        var openTickets = ticketingService.getAllOpenClaimedTickets(page, size);

        if (openTickets.isEmpty()) {
            logger.error("No open tickets found");
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(openTickets, HttpStatus.OK);
    }

    @GetMapping("/unclaimed")
    private ResponseEntity<List<ServiceTicketDto>> getOpenUnclaimedTickets(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "15") int size
    ) {
        var openTickets = ticketingService.getAllOpenUnclaimedTickets(page, size);

        if (openTickets.isEmpty()) {
            logger.error("No open tickets found");
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(openTickets, HttpStatus.OK);
    }

    @GetMapping("/open-count")
    private ResponseEntity<Long> getOpenCountTickets() {
        var count = ticketingService.getTotalOpenTickets();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/closed-count")
    private ResponseEntity<Long> getClosedCountTickets() {
        var count = ticketingService.getTotalClosedTickets();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/recent")
    private ResponseEntity<List<ServiceTicketDto>> getRecentTickets() {
        var recentTickets = ticketingService.findAllRecentTickets();
        if (recentTickets.isEmpty()) {
            logger.error("No recent tickets found");
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(recentTickets, HttpStatus.OK);
    }
}
