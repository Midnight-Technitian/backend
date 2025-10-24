package dev.glabay.ticketing.controllers;

import dev.glabay.dtos.ServiceTicketDto;
import dev.glabay.models.request.ServiceRequest;
import dev.glabay.ticketing.services.TicketingService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final Logger logger = LoggerFactory.getLogger(TicketingController.class);

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

        return new ResponseEntity<>(ticket.mapToDto(), HttpStatus.CREATED);
    }


    @GetMapping("/customer")
    private ResponseEntity<List<ServiceTicketDto>> getOpenTicketsForEmail(
        @RequestParam("email") String email
    ) {
        var openTickets = ticketingService.getAllOpenTickets(email);

        if (openTickets.isEmpty())
            return ResponseEntity.notFound().build();

        return new ResponseEntity<>(openTickets, HttpStatus.OK);
    }

    @GetMapping("/open")
    private ResponseEntity<List<ServiceTicketDto>> getOpenTickets() {
        // TODO: Paging... return by default 0-15
        var openTickets = ticketingService.getAllOpenTickets();

        if (openTickets.isEmpty())
            return ResponseEntity.notFound().build();

        return new ResponseEntity<>(openTickets, HttpStatus.OK);
    }
}
