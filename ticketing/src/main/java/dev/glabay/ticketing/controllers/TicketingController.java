package dev.glabay.ticketing.controllers;

import dev.glabay.models.request.ServiceRequest;
import dev.glabay.ticketing.models.ServiceTicket;
import dev.glabay.ticketing.services.TicketingService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin(origins = "http://localhost:80")
public class TicketingController {
    private final Logger logger = LoggerFactory.getLogger(TicketingController.class);

    private final TicketingService ticketingService;


    @PostMapping()
    private ResponseEntity<ServiceTicket> postNewServiceTicket(@RequestBody ServiceRequest requestDto) {
        if (requestDto.serviceDescription().isBlank()) {
            logger.error("Service description is blank");
            return ResponseEntity.badRequest().build();
        }
        if (requestDto.customerEmail().isBlank()) {
            logger.error("Customer email is blank");
            return ResponseEntity.badRequest().build();
        }

        var ticket = ticketingService.createServiceTicket(requestDto);
        return new ResponseEntity<>(ticket, HttpStatus.CREATED);
    }

}
