package dev.glabay.controllers;

import dev.glabay.dtos.ServiceTicketDto;
import dev.glabay.models.ServiceNote;
import dev.glabay.models.request.ServiceRequest;
import dev.glabay.services.KafkaEventService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-28
 */
@NullMarked
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/service-ticket")
public class TicketController {
    private final KafkaEventService kafkaEventService;
    private final RestClient restClient;

    @PostMapping("/tickets")
    private String postNewTicket(@RequestBody ServiceRequest body) {
        kafkaEventService.publishServiceTicketRequest(body);
        return "redirect:/dashboard";
    }

    @PostMapping()
    private String updateServiceTicket(@RequestBody ServiceTicketDto body) {
        var serviceNoteDto = restClient.put()
            .uri("http://localhost:8081/api/v1/tickets")
            .body(body)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ServiceTicketDto>() {})
            .getBody();

        if (serviceNoteDto == null)
            return "redirect:/error";

        return "redirect:/dashboard/ticket?id=".concat(serviceNoteDto.getTicketId());
    }

    @PostMapping("/notes")
    private String postNewServiceTicketNotes(@RequestBody ServiceNote body) {
        var serviceNoteDto = restClient.post()
            .uri("http://localhost:8081/api/v1/tickets/note")
            .body(body)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<ServiceTicketDto>() {})
            .getBody();

        if (serviceNoteDto == null)
            return "redirect:/error";

        return "redirect:/dashboard/ticket?id=".concat(serviceNoteDto.getTicketId());
    }

    @PostMapping("/close")
    private String closeServiceTicket(@RequestParam("ticketId") String ticketId) {
        // TODO: Validate the provided String matches the expect pattern: 'TICKET-1'
        var responseSpec = restClient.put()
            .uri("http://localhost:8081/api/v1/tickets/close?ticketId={ticketId}", ticketId)
            .retrieve();

        if (responseSpec.toBodilessEntity().getStatusCode() != HttpStatus.OK)
            return "redirect:/error";

        return "redirect:/dashboard/ticketing";
    }

    @PostMapping("/assign")
    private String claimServiceTicket(
        @RequestParam("employeeId") String employeeId,
        @RequestParam("ticketId") String ticketId
    ) {
        // TODO: Validate the provided String matches the expect pattern: 'TICKET-1', 'EMPLOYEE-1'
        var responseSpec = restClient.put()
            .uri("http://localhost:8081/api/v1/tickets/claim?" +
                    "ticketId={ticketId}&" +
                    "employeeId={employeeId}",
                ticketId,
                employeeId
            ).retrieve();

        if (responseSpec.toBodilessEntity().getStatusCode() != HttpStatus.OK)
            return "redirect:/error";

        return "redirect:/dashboard/ticket?id=".concat(ticketId);
    }
}
