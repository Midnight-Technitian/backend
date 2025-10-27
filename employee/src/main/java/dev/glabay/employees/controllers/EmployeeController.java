package dev.glabay.employees.controllers;

import dev.glabay.dtos.EmployeeDto;
import dev.glabay.employees.services.EmployeeConverter;
import dev.glabay.employees.services.EmployeeService;
import dev.glabay.logging.MidnightLogger;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
*/
@NullMarked
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController implements EmployeeConverter {
    private final Logger logger = MidnightLogger.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    @PostMapping
    private ResponseEntity<EmployeeDto> registerNewEmployee(@RequestBody EmployeeDto dto) {
        if (employeeService.employeeExists(dto.email())) {
            logger.info("Employee already exists with email: {}", dto.email());
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build();
        }
        var reply = employeeService.createNewEmployee(dto);
        if (reply == null) {
            logger.error("Error creating employee with email: {}", dto.email());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return new ResponseEntity<>(mapToDto(reply), HttpStatus.CREATED);
    }

    @PutMapping
    private ResponseEntity<EmployeeDto> updateEmployee(@RequestBody EmployeeDto dto) {
        if (!employeeService.employeeExists(dto.email())) {
            logger.info("Employee does not exist with email: {}", dto.email());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var reply = employeeService.updateEmployee(dto);
        if (reply == null) {
            logger.error("Error updating employee with email: {}", dto.email());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return new ResponseEntity<>(mapToDto(reply), HttpStatus.OK);
    }

    @GetMapping
    private ResponseEntity<EmployeeDto> getEmployee(@RequestParam("email") String email) {
        // TODO: Verify the email is an email
        if (!employeeService.employeeExists(email)) {
            logger.info("Employee does not exist with email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var dto = employeeService.findEmployeeDtoByEmail(email);
        if (dto == null) {
            logger.error("Error getting employee with email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/all")
    private ResponseEntity<List<EmployeeDto>> getEmployees() {
        var employeeDtoList = employeeService.getEmployees();
        return new ResponseEntity<>(employeeDtoList, HttpStatus.OK);
    }

}
