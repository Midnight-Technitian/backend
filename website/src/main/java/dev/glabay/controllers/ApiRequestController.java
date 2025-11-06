package dev.glabay.controllers;

import dev.glabay.dtos.EmployeeDto;
import dev.glabay.dtos.ServiceTicketDto;
import dev.glabay.kafka.events.CustomerDeviceRegistrationEvent;
import dev.glabay.kafka.KafkaTopics;
import dev.glabay.models.ServiceNote;
import dev.glabay.models.device.RegisteringDevice;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-26
 */
@NullMarked
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiRequestController {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RestClient restClient;

    @PostMapping("/device")
    private String postNewDevice(@RequestBody RegisteringDevice body) {
        var event = new CustomerDeviceRegistrationEvent(body);
        kafkaTemplate.send(KafkaTopics.CUSTOMER_DEVICE_REGISTRATION.getTopicName(), body.getCustomerEmail(), event);
        return "redirect:/dashboard";
    }

    @PostMapping("/employee")
    private String postNewEmployee(@RequestBody EmployeeDto body) {
        var deviceDto = restClient.post()
            .uri("http://localhost:8082/api/v1/employees")
            .body(body)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<EmployeeDto>() {})
            .getBody();

        if (deviceDto == null)
            return "redirect:/error";

        return "redirect:/dashboard/admin";
    }

}
