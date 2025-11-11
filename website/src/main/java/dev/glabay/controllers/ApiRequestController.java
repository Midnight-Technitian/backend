package dev.glabay.controllers;

import dev.glabay.dtos.CustomerCreatedDto;
import dev.glabay.dtos.CustomerDeviceDto;
import dev.glabay.dtos.CustomerDto;
import dev.glabay.dtos.EmployeeDto;
import dev.glabay.logging.MidnightLogger;
import dev.glabay.models.device.RegisteringDevice;
import dev.glabay.services.KafkaEventService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

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
    private final Logger logger = MidnightLogger.getLogger(ApiRequestController.class);

    private final KafkaEventService kafkaEventService;
    private final RestClient restClient;

    @PostMapping("/device")
    private String postNewDevice(@RequestBody RegisteringDevice body) {
        kafkaEventService.publishDeviceRegistration(body);
        logger.info("New Device Registration Event sent to Kafka {}", body);
        return "redirect:/dashboard";
    }

    @PostMapping("/employee")
    private String postNewEmployee(@RequestBody EmployeeDto body) {
        var deviceDto = restClient.post()
            .uri("http://localhost:8082/api/v1/employees")
            .body(body)
            .retrieve()
            .body(new ParameterizedTypeReference<EmployeeDto>() {});

        if (deviceDto == null) {
            logger.error("Failed to register employee: {}", body);
            return "redirect:/error";
        }

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
            logger.info("Employee punched in successfully");
            return ResponseEntity.ok().build();
        }
        catch (ConflictException e) {
            logger.warn("Conflict occurred while punching in employee: {}", employeeId);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (Exception e) {
            logger.error("Failed to punch in employee: {}", employeeId, e);
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
            logger.info("Employee punched out successfully");
            return ResponseEntity.ok().build();
        }
        catch (ConflictException e) {
            logger.warn("Conflict occurred while punching out employee: {}", employeeId);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch (Exception e) {
            logger.error("Failed to punch out employee: {}", employeeId, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    @GetMapping("/customer/search")
    private ResponseEntity<CustomerDto> searchCustomerByEmail(@RequestParam("email") String email) {
        try {
            var customer = restClient.get()
                .uri("http://localhost:8083/api/v1/customers/email?email={email}", email)
                .retrieve()
                .body(CustomerDto.class);
            logger.info("Customer found by email: {}", email);
            return ResponseEntity.ok(customer);
        }
        catch (Exception e) {
            logger.error("Failed to retrieve customer data for email: {}", email);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/device")
    private ResponseEntity<List<CustomerDeviceDto>> getCustomerDeviceByEmail(@RequestParam("email") String email) {
        try {
            var devices = restClient.get()
                .uri("http://localhost:8084/api/v1/devices?email={email}", email)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CustomerDeviceDto>>() {});
            logger.info("Customer devices retrieved successfully");
            return ResponseEntity.ok(devices);
        }
        catch (Exception e) {
            logger.error("Failed to retrieve customer devices for email: {}", email);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/customer")
    private ResponseEntity<Void> registerCustomerFromEmployeeDashboard(@RequestBody CustomerCreatedDto body) {
        kafkaEventService.publishCustomerRegistration(
            body.customerEmail(),
            body.firstName(),
            body.lastName(),
            body.contactNumber(),
            body.registeredBy()
        );
        logger.info("Customer registered from employee dashboard {}", body);
        return ResponseEntity.ok().build();
    }

    private static class ConflictException extends RuntimeException {}
}
