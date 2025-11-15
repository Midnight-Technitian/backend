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
            .uri("http://customer/api/v1/customers/email?email={email}", email)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomerDto>() {});

        var ticketResponseSpec = restClient.get()
            .uri("http://ticketing/api/v1/tickets?ticketId={ticketId}", ticketId)
            .retrieve();

        var ticketStatus = ticketResponseSpec.toBodilessEntity().getStatusCode();
        if (ticketStatus == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalServiceTicketDto = Optional.ofNullable(ticketResponseSpec.body(new ParameterizedTypeReference<ServiceTicketDto>() {}));
        if (optionalServiceTicketDto.isEmpty())
            return "redirect:/error";
        var serviceTicketDto = optionalServiceTicketDto.get();

        // TODO: Check if the Ticket is connected with the Customer, else redirect to error page

        var deviceResponseSpec = restClient.get()
            .uri("http://customer-device/api/v1/devices/device?deviceId={deviceId}", serviceTicketDto.getCustomerDeviceId())
                .retrieve();

        var deviceStatus = deviceResponseSpec.toBodilessEntity().getStatusCode();
        if (deviceStatus == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalDeviceDto = Optional.ofNullable(deviceResponseSpec.body(new ParameterizedTypeReference<CustomerDeviceDto>() {}));
        if (optionalDeviceDto.isEmpty())
            return "redirect:/error";

        var deviceDto = optionalDeviceDto.get();

        var serviceResponseSpec = restClient.get()
            .uri("http://services/api/v1/services/{serviceId}", serviceTicketDto.getServiceId())
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


    @GetMapping("/user/ticket/history")
    @PreAuthorize("hasRole('USER')")
    public String getCustomerTicketHistoryDashboard(HttpServletRequest request, Model model) {
        var email = request.getRemoteUser();
        var customerDto = restClient.get()
            .uri("http://customer/api/v1/customers/email?email={email}", email)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomerDto>() {});

        var ticketHistory = restClient.get()
            .uri("http://ticketing/api/v1/tickets/customer/history?email={email}", email)
            .retrieve()
            .body(new ParameterizedTypeReference<List<ServiceTicketDto>>() {});

        model.addAttribute("customer", customerDto);
        model.addAttribute("ticketHistory", ticketHistory);
        return "dashboards/customer/ticket_history_view";
    }

    @GetMapping("/user/ticket/device")
    @PreAuthorize("hasRole('USER')")
    public String getCustomerTicketHistoryForDeviceDashboard(
        @RequestParam("id") String deviceId,
        HttpServletRequest request,
        Model model
    ) {
        var email = request.getRemoteUser();
        var customerDto = restClient.get()
            .uri("http://customer/api/v1/customers/email?email={email}", email)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomerDto>() {});

        // fetch customer Devices (up to a maximum of 6)
        var device = restClient.get()
            .uri("http://customer-device/api/v1/devices/device?deviceId={deviceId}", deviceId)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomerDeviceDto>() {});

        var ticketHistory = restClient.get()
            .uri("http://ticketing/api/v1/tickets/customer/device?deviceId={deviceId}", deviceId)
            .retrieve()
            .body(new ParameterizedTypeReference<List<ServiceTicketDto>>() {});

        model.addAttribute("customer", customerDto);
        model.addAttribute("ticketHistory", ticketHistory);
        model.addAttribute("device", device);
        return "dashboards/customer/ticket_device_view";
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public String getDashboard(HttpServletRequest request, Model model) {
        var email = request.getRemoteUser();
        // fetch the customer data object
        var customerDto = restClient.get()
            .uri("http://customer/api/v1/customers/email?email={email}", email)
            .retrieve()
            .body(new ParameterizedTypeReference<CustomerDto>() {});
        // fetch customer Open Service Tickets (up to a maximum of 6)
        var openTickets = restClient.get()
            .uri("http://ticketing/api/v1/tickets/customer?email={email}", email)
            .retrieve()
            .body(new ParameterizedTypeReference<List<ServiceTicketDto>>() {});
        // fetch customer Devices (up to a maximum of 6)
        var devices = getCustomerDevices(email);

        var unhealthyDevices = new ArrayList<Long>();
        for (var ticket : openTickets) {
            var deviceIdAsString = ticket.getCustomerDeviceId();
            if (deviceIdAsString == null) continue;
            var deviceId = Long.parseLong(deviceIdAsString);
            unhealthyDevices.add(deviceId);
        }

        model.addAttribute("deviceTypes", getDeviceTypes());
        model.addAttribute("customerEmail", email);
        model.addAttribute("customer", customerDto);
        model.addAttribute("services", getServices());
        model.addAttribute("openTickets", openTickets);
        model.addAttribute("devices", devices);
        model.addAttribute("devicesInRepair", unhealthyDevices);
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
            .uri("http://ticketing/api/v1/tickets?ticketId={ticketId}", ticketId)
            .retrieve();

        var ticketStatus = ticketResponseSpec.toBodilessEntity().getStatusCode();
        if (ticketStatus == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalServiceTicketDto = Optional.ofNullable(ticketResponseSpec.body(new ParameterizedTypeReference<ServiceTicketDto>() {}));
        if (optionalServiceTicketDto.isEmpty())
            return "redirect:/error";
        var serviceTicketDto = optionalServiceTicketDto.get();

        var deviceResponseSpec = restClient.get()
            .uri("http://customer-device/api/v1/devices/device?deviceId={deviceId}", serviceTicketDto.getCustomerDeviceId())
            .retrieve();

        var deviceStatus = deviceResponseSpec.toBodilessEntity().getStatusCode();
        if (deviceStatus == HttpStatus.NOT_FOUND)
            return "redirect:/error";

        var optionalDeviceDto = Optional.ofNullable(deviceResponseSpec.body(new ParameterizedTypeReference<CustomerDeviceDto>() {}));
        if (optionalDeviceDto.isEmpty())
            return "redirect:/error";

        var deviceDto = optionalDeviceDto.get();

        var serviceResponseSpec = restClient.get()
            .uri("http://services/api/v1/services/{serviceId}", serviceTicketDto.getServiceId())
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
            .uri("http://ticketing/api/v1/tickets/unclaimed")
            .retrieve()
            .body(new ParameterizedTypeReference<List<ServiceTicketDto>>() {});

        var employeeActiveTickets = restClient.get()
            .uri("http://ticketing/api/v1/tickets/employee?employeeId={employeeId}", employeeDto.get().employeeId())
            .retrieve()
            .body(new ParameterizedTypeReference<List<ServiceTicketDto>>() {});

        var claimedTickets = restClient.get()
            .uri("http://ticketing/api/v1/tickets/claimed")
            .retrieve()
            .body(new ParameterizedTypeReference<List<ServiceTicketDto>>() {});

        model.addAttribute("services", getServices());
        model.addAttribute("deviceTypes", getDeviceTypes());
        model.addAttribute("devices", getCustomerDevices(email));
        model.addAttribute("employee", employeeDto.get());
        model.addAttribute("employeeActiveTickets", Objects.isNull(employeeActiveTickets) ? List.of() : employeeActiveTickets);
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
            .uri("http://employee/api/v1/employees/all")
            .retrieve()
            .body(new ParameterizedTypeReference<List<EmployeeDto>>() {});

        var customers = restClient.get()
            .uri("http://customer/api/v1/customers")
            .retrieve()
            .body(new ParameterizedTypeReference<List<CustomerDto>>() {});

        var recentTickets = restClient.get()
            .uri("http://ticketing/api/v1/tickets/recent")
            .retrieve()
            .body(new ParameterizedTypeReference<List<ServiceTicketDto>>() {});

        var openCount = restClient.get()
            .uri("http://ticketing/api/v1/tickets/open-count")
            .retrieve()
            .toEntity(Long.class)
            .getBody();

        var closedCount = restClient.get()
            .uri("http://ticketing/api/v1/tickets/closed-count")
            .retrieve()
            .body(Long.class);

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

    @GetMapping("/admin/employee")
    @PreAuthorize("hasRole('MANAGER')")
    public String getEmployeeProfile(
        @RequestParam("id") String employeeId,
        HttpServletRequest request,
        Model model
    ) {
        var email = request.getRemoteUser();
        var managerDto = getEmployee(email);
        if (managerDto.isEmpty()) {
            logger.debug("Failed to retrieve manager data for email: {}", email);
            return "redirect:/error";
        }

        var employeeResponseSpec = restClient.get()
            .uri("http://employee/api/v1/employees/single?employeeId={employeeId}", employeeId)
            .retrieve();

        var employeeStatus = employeeResponseSpec.toBodilessEntity().getStatusCode();
        if (employeeStatus == HttpStatus.NOT_FOUND) {
            logger.debug("Employee with ID {} not found", employeeId);
            return "redirect:/error";
        }

        var employeeDto = employeeResponseSpec.body(new ParameterizedTypeReference<EmployeeDto>() {});
        if (employeeDto == null) {
            logger.debug("Failed to retrieve employee data for ID: {}", employeeId);
            return "redirect:/error";
        }

        // Fetch current week schedule for this employee
        var currentWeekStart = java.time.LocalDate.now()
            .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        var currentWeekEnd = currentWeekStart.plusDays(6);

        var scheduleResponseSpec = restClient.get()
            .uri("http://scheduling/api/v1/schedules/{employeeId}", employeeId)
            .retrieve();

        var scheduleMap = new java.util.HashMap<String, dev.glabay.dtos.Shift>();

        // Only process if we got a successful response
        var scheduleStatus = scheduleResponseSpec.toBodilessEntity().getStatusCode();
        if (scheduleStatus.is2xxSuccessful()) {
            var scheduleDto = scheduleResponseSpec.body(new ParameterizedTypeReference<ScheduleDto>() {});
            if (scheduleDto != null && scheduleDto.shifts() != null) {
                for (var shift : scheduleDto.shifts()) {
                    scheduleMap.put(shift.day().name(), shift);
                }
            }
        }

        // Format dates for display
        var dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy");

        model.addAttribute("manager", managerDto.get());
        model.addAttribute("employee", employeeDto);
        model.addAttribute("scheduleMap", scheduleMap);
        model.addAttribute("currentWeekStart", currentWeekStart.format(dateFormatter));
        model.addAttribute("currentWeekEnd", currentWeekEnd.format(dateFormatter));

        return "dashboards/admin/employee_profile_view";
    }

    @GetMapping("/admin/schedules")
    @PreAuthorize("hasRole('MANAGER')")
    public String getScheduleManagement(HttpServletRequest request, Model model) {
        var email = request.getRemoteUser();
        var managerDto = getEmployee(email);
        if (managerDto.isEmpty()) {
            logger.debug("Failed to retrieve manager data for email: {}", email);
            return "redirect:/error";
        }

        var employees = restClient.get()
            .uri("http://employee/api/v1/employees/all")
            .retrieve()
            .body(new ParameterizedTypeReference<List<EmployeeDto>>() {});

        // Calculate current week and next week dates
        var currentWeekStart = java.time.LocalDate.now()
            .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        var currentWeekEnd = currentWeekStart.plusDays(6);
        var nextWeekStart = currentWeekStart.plusDays(7);
        var nextWeekEnd = nextWeekStart.plusDays(6);

        // Fetch schedules for current week
        var currentWeekSchedules = restClient.get()
            .uri("http://scheduling/api/v1/schedules/week?weekStart={weekStart}", currentWeekStart)
            .retrieve()
            .body(new ParameterizedTypeReference<List<ScheduleDto>>() {});

        // Fetch schedules for next week
        var nextWeekSchedules = restClient.get()
            .uri("http://scheduling/api/v1/schedules/week?weekStart={weekStart}", nextWeekStart)
            .retrieve()
            .body(new ParameterizedTypeReference<List<ScheduleDto>>() {});

        // Create maps for easy lookup: "employeeId_DAY" -> Shift
        var currentWeekScheduleMap = createScheduleMap(currentWeekSchedules);
        var nextWeekScheduleMap = createScheduleMap(nextWeekSchedules);

        // Format dates for display
        var dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy");

        model.addAttribute("manager", managerDto.get());
        model.addAttribute("employees", Objects.isNull(employees) ? List.of() : employees);
        model.addAttribute("currentWeekScheduleMap", currentWeekScheduleMap);
        model.addAttribute("nextWeekScheduleMap", nextWeekScheduleMap);
        model.addAttribute("currentWeekStart", currentWeekStart.format(dateFormatter));
        model.addAttribute("currentWeekEnd", currentWeekEnd.format(dateFormatter));
        model.addAttribute("nextWeekStart", nextWeekStart.format(dateFormatter));
        model.addAttribute("nextWeekEnd", nextWeekEnd.format(dateFormatter));

        return "dashboards/admin/schedule_management";
    }


    private Collection<ServiceDto> getServices() {
        var services = restClient.get()
            .uri("http://services/api/v1/services")
            .retrieve()
            .body(new ParameterizedTypeReference<Collection<ServiceDto>>() {});
        if (services == null)
            return List.of();
        return services;
    }

    private List<DeviceType> getDeviceTypes() {
        return List.of(DeviceType.values());
    }

    private List<CustomerDeviceDto> getCustomerDevices(String email) {
        var devices = new ArrayList<CustomerDeviceDto>();
        var deviceResponseSpec = restClient.get()
            .uri("http://customer-device/api/v1/devices?email={email}", email)
            .retrieve();

        var deviceStatus = deviceResponseSpec.toBodilessEntity().getStatusCode();
        if (deviceStatus.is2xxSuccessful())
            devices = deviceResponseSpec.body(new ParameterizedTypeReference<>() {});
        if (devices == null)
            return List.of();
        return devices;
    }

    private Optional<EmployeeDto> getEmployee(String email) {
        var responseSpec = restClient.get()
            .uri("http://employee/api/v1/employees?email={email}", email)
            .retrieve();

        var status = responseSpec.toBodilessEntity().getStatusCode();
        if (status == HttpStatus.NOT_FOUND) {
            logger.debug("Failed to retrieve employee data for email: {} returning empty Optional", email);
            return Optional.empty();
        }
        return Optional.ofNullable(responseSpec.body(new ParameterizedTypeReference<>() {}));
    }

    private Map<String, Shift> createScheduleMap(List<ScheduleDto> schedules) {
        var scheduleMap = new HashMap<String, Shift>();
        for (var schedule : schedules) {
            var employeeId = schedule.employeeId();
            if (schedule.shifts() != null) {
                for (var shift : schedule.shifts()) {
                    var key = employeeId + "_" + shift.day().name();
                    scheduleMap.put(key, shift);
                }
            }
        }
        return scheduleMap;
    }
}
