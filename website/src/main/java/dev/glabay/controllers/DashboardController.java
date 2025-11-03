package dev.glabay.controllers;

import dev.glabay.dtos.*;
import dev.glabay.logging.MidnightLogger;
import dev.glabay.models.device.DeviceType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.util.*;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@NullMarked
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final Logger logger = MidnightLogger.getLogger(DashboardController.class);

    private final RestClient restClient;

    @GetMapping("/user/ticket")
    @PreAuthorize("hasRole('USER')")
    public String getCustomerTicketDashboard(
        @RequestParam("id") String ticketId,
        HttpServletRequest request,
        Model model
    ) {
        var email = request.getRemoteUser();
        // fetch the customer data object
        var customerDto = restClient.get()
            .uri("http://localhost:8083/api/v1/customers/email?email={email}", email)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<CustomerDto>() {})
            .getBody();

        var ticketResponseSpec = restClient.get()
            .uri("http://localhost:8081/api/v1/tickets?ticketId={ticketId}", ticketId)
            .retrieve();

        var ticketStatus = ticketResponseSpec.toBodilessEntity().getStatusCode();
        if (ticketStatus == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalServiceTicketDto = Optional.ofNullable(ticketResponseSpec.body(new ParameterizedTypeReference<ServiceTicketDto>() {}));
        if (optionalServiceTicketDto.isEmpty())
            return "redirect:/error";
        var serviceTicketDto = optionalServiceTicketDto.get();

        var deviceResponseSpec = restClient.get()
            .uri("http://localhost:8084/api/v1/devices/device?deviceId={deviceId}", serviceTicketDto.getCustomerDeviceId())
                .retrieve();

        var deviceStatus = deviceResponseSpec.toBodilessEntity().getStatusCode();
        if (deviceStatus == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalDeviceDto = Optional.ofNullable(deviceResponseSpec.body(new ParameterizedTypeReference<CustomerDeviceDto>() {}));
        if (optionalDeviceDto.isEmpty())
            return "redirect:/error";

        var deviceDto = optionalDeviceDto.get();

        var serviceResponseSpec = restClient.get()
            .uri("http://localhost:8080/api/v1/services/{serviceId}", serviceTicketDto.getServiceId())
                .retrieve();

        var serviceStatusCode = serviceResponseSpec.toBodilessEntity().getStatusCode();
        if (serviceStatusCode == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalServiceDto = Optional.ofNullable(serviceResponseSpec.body(new ParameterizedTypeReference<ServiceDto>() {}));
        if (optionalServiceDto.isEmpty())
            return "redirect:/error";

        var serviceDto = optionalServiceDto.get();

        model.addAttribute("customer", customerDto);
        model.addAttribute("serviceTicket", serviceTicketDto);
        model.addAttribute("device", deviceDto);
        model.addAttribute("service", serviceDto);
        return "dashboards/customer/ticket_view";
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String getDashboard(HttpServletRequest request, Model model) {
        var email = request.getRemoteUser();
        // fetch the customer data object
        var customerDto = restClient.get()
            .uri("http://localhost:8083/api/v1/customers/email?email={email}", email)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<CustomerDto>() {})
            .getBody();
        // fetch a list of available services
        var services = restClient.get()
            .uri("http://localhost:8080/api/v1/services")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<Collection<ServiceDto>>() {})
            .getBody();
        // fetch customer Open Service Tickets (up to a maximum of 6)
        var openTickets = restClient.get()
            .uri("http://localhost:8081/api/v1/tickets/customer?email={email}", email)
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ServiceTicketDto>>() {})
            .getBody();
        // fetch customer Devices (up to a maximum of 6)
        var devices = new ArrayList<CustomerDeviceDto>();
        var deviceResponseSpec = restClient.get()
            .uri("http://localhost:8084/api/v1/devices?email={email}", email)
            .retrieve();

        var deviceStatus = deviceResponseSpec.toBodilessEntity().getStatusCode();
        if (deviceStatus.is2xxSuccessful())
            devices = deviceResponseSpec.body(new ParameterizedTypeReference<>() {});

        var deviceTypes = List.of(DeviceType.values());

        model.addAttribute("deviceTypes", deviceTypes);
        model.addAttribute("customerEmail", email);
        model.addAttribute("customer", customerDto);
        model.addAttribute("services", services);
        model.addAttribute("openTickets", openTickets);
        model.addAttribute("devices", devices);
        return "dashboards/customer/dashboard";
    }

    @GetMapping("/ticket")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public String getTicketDashboard(
        @RequestParam("id") String ticketId,
        HttpServletRequest request,
        Model model
    ) {
        var email = request.getRemoteUser();

        var employeeDto = getEmployee(email);
        if (employeeDto.isEmpty()) {
            logger.debug("Failed to retrieve employee data for email: {}", email);
            return "redirect:/error";
        }

        var ticketResponseSpec = restClient.get()
            .uri("http://localhost:8081/api/v1/tickets?ticketId={ticketId}", ticketId)
            .retrieve();

        var ticketStatus = ticketResponseSpec.toBodilessEntity().getStatusCode();
        if (ticketStatus == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalServiceTicketDto = Optional.ofNullable(ticketResponseSpec.body(new ParameterizedTypeReference<ServiceTicketDto>() {}));
        if (optionalServiceTicketDto.isEmpty())
            return "redirect:/error";
        var serviceTicketDto = optionalServiceTicketDto.get();

        var deviceResponseSpec = restClient.get()
            .uri("http://localhost:8084/api/v1/devices/device?deviceId={deviceId}", serviceTicketDto.getCustomerDeviceId())
            .retrieve();

        var deviceStatus = deviceResponseSpec.toBodilessEntity().getStatusCode();
        if (deviceStatus == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalDeviceDto = Optional.ofNullable(deviceResponseSpec.body(new ParameterizedTypeReference<CustomerDeviceDto>() {}));
        if (optionalDeviceDto.isEmpty())
            return "redirect:/error";

        var deviceDto = optionalDeviceDto.get();

        var serviceResponseSpec = restClient.get()
            .uri("http://localhost:8080/api/v1/services/{serviceId}", serviceTicketDto.getServiceId())
            .retrieve();

        var serviceStatusCode = serviceResponseSpec.toBodilessEntity().getStatusCode();
        if (serviceStatusCode == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalServiceDto = Optional.ofNullable(serviceResponseSpec.body(new ParameterizedTypeReference<ServiceDto>() {}));
        if (optionalServiceDto.isEmpty())
            return "redirect:/error";

        var serviceDto = optionalServiceDto.get();

        model.addAttribute("employee", employeeDto.get());
        model.addAttribute("serviceTicket", serviceTicketDto);
        model.addAttribute("device", deviceDto);
        model.addAttribute("service", serviceDto);
        return "dashboards/tickets/ticket_view";
    }

    @GetMapping("/ticketing")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public String getTicketingDashboard(HttpServletRequest request, Model model) {
        // get the email of the user to fetch their employee record
        var email = request.getRemoteUser();

        var employeeDto = getEmployee(email);
        if (employeeDto.isEmpty()) {
            logger.debug("Failed to retrieve employee data for email: {}", email);
            return "redirect:/error";
        }

        var openTickets = restClient.get()
            .uri("http://localhost:8081/api/v1/tickets/unclaimed")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ServiceTicketDto>>() {})
            .getBody();

        var claimedTickets = restClient.get()
            .uri("http://localhost:8081/api/v1/tickets/claimed")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ServiceTicketDto>>() {})
            .getBody();

        model.addAttribute("employee", employeeDto.get());
        model.addAttribute("openTickets", Objects.isNull(openTickets) ? List.of() : openTickets);
        model.addAttribute("claimedTickets", Objects.isNull(claimedTickets) ? List.of() : claimedTickets);
        return "dashboards/tickets/dashboard";
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('MANAGER')")
    public String getAdminDashboard(HttpServletRequest request, Model model) {
        var email = request.getRemoteUser();
        var employeeDto = getEmployee(email);
        if (employeeDto.isEmpty()) {
            logger.debug("Failed to retrieve employee data for email: {}", email);
            return "redirect:/error";
        }

        var employees = restClient.get()
            .uri("http://localhost:8082/api/v1/employees/all")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<EmployeeDto>>() {})
            .getBody();

        var customers = restClient.get()
            .uri("http://localhost:8083/api/v1/customers")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<CustomerDto>>() {})
            .getBody();

        var recentTickets = restClient.get()
            .uri("http://localhost:8081/api/v1/tickets/recent")
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<ServiceTicketDto>>() {})
            .getBody();

        var openCount = restClient.get()
            .uri("http://localhost:8081/api/v1/tickets/open-count")
            .retrieve()
            .toEntity(Long.class)
            .getBody();

        var closedCount = restClient.get()
            .uri("http://localhost:8081/api/v1/tickets/closed-count")
            .retrieve()
            .toEntity(Long.class)
            .getBody();

        var emptyEmployee = new EmployeeDto(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            null,
            null,
            null,
            null,
            null,
            null
        );

        model.addAttribute("employee", employeeDto.get());
        model.addAttribute("newEmployee", emptyEmployee);
        model.addAttribute("employees", Objects.isNull(employees) ? List.of() : employees);
        model.addAttribute("customers", Objects.isNull(customers) ? List.of() : customers);
        model.addAttribute("recentTickets", Objects.isNull(recentTickets) ? List.of() : recentTickets);
        model.addAttribute("activeTicketCount", openCount);
        model.addAttribute("resolvedTicketCount", closedCount);
        return "dashboards/admin/dashboard";
    }


    private Optional<EmployeeDto> getEmployee(String email) {
        var responseSpec = restClient.get()
            .uri("http://localhost:8082/api/v1/employees?email={email}", email)
            .retrieve();

        var status = responseSpec.toBodilessEntity().getStatusCode();
        if (status == HttpStatus.NOT_FOUND) {
            logger.debug("Failed to retrieve employee data for email: {} returning empty Optional", email);
            return Optional.empty();
        }
        return Optional.ofNullable(responseSpec.body(new ParameterizedTypeReference<>() {}));
    }

}
