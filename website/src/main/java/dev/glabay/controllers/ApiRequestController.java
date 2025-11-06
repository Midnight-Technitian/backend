package dev.glabay.controllers;

import dev.glabay.dtos.EmployeeDto;
import dev.glabay.models.device.RegisteringDevice;
import dev.glabay.services.KafkaEventService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    private final KafkaEventService kafkaEventService;
    private final RestClient restClient;

    @PostMapping("/device")
    private String postNewDevice(@RequestBody RegisteringDevice body) {
        kafkaEventService.publishDeviceRegistration(body);
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

    @PostMapping("/schedule/punch-in")
    private ResponseEntity<Void> punchIn(@RequestParam("employeeId") String employeeId) {
        try {
            restClient.post()
                .uri("http://localhost:8086/api/v1/schedule/clock-in?employeeId={employeeId}", employeeId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    (_, response) -> {
                        if (response.getStatusCode() == HttpStatus.CONFLICT)
                            throw new ConflictException();
                    })
                .toBodilessEntity();
            return ResponseEntity.ok().build();
        }
        catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @PostMapping("/schedule/punch-out")
    private ResponseEntity<Void> punchOut(@RequestParam("employeeId") String employeeId) {
        try {
            restClient.post()
                .uri("http://localhost:8086/api/v1/schedule/clock-out?employeeId={employeeId}", employeeId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    (_, response) -> {
                        if (response.getStatusCode() == HttpStatus.CONFLICT)
                            throw new ConflictException();
                    })
                .toBodilessEntity();
            return ResponseEntity.ok().build();
        }
        catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }


    private static class ConflictException extends RuntimeException {}
}
