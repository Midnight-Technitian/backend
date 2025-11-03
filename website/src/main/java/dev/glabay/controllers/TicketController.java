package dev.glabay.controllers;

import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.events.ServiceTicketCreationEvent;
import dev.glabay.models.request.ServiceRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-28
 */
@NullMarked
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/tickets")
public class TicketController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping
    private String postNewTicket(@RequestBody ServiceRequest body) {
        var event = new ServiceTicketCreationEvent(body);
        kafkaTemplate.send(KafkaTopics.SERVICE_TICKET_CREATION.getTopicName(), body.customerEmail(), event);
        return "redirect:/dashboard";
    }
}
