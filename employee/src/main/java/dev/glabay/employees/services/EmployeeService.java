package dev.glabay.employees.services;

import dev.glabay.dtos.EmployeeDto;
import dev.glabay.employees.models.Employee;
import dev.glabay.employees.repos.EmployeeRepository;
import dev.glabay.services.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
*/
@Service
@RequiredArgsConstructor
public class EmployeeService implements EmployeeConverter {
    private final EmployeeRepository employeeRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    public boolean employeeExists(String email) {
        return employeeRepository.existsByEmailIgnoreCase(email);
    }

    public EmployeeDto findEmployeeDtoByEmail(String email) {
        var optionalEmployee = findEmployeeByEmail(email);
        if (optionalEmployee == null)
            return null;
        return mapToDto(optionalEmployee);
    }

    private Employee findEmployeeByEmail(String email) {
        var optionalEmployee = employeeRepository.findByEmailIgnoreCase(email);
        return optionalEmployee
            .orElse(null);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    /// some things in here are specifically redacted from modification
    public Employee updateEmployee(EmployeeDto dto) {
        var model = findEmployeeByEmail(dto.email());
            model.setFirstName(dto.firstName());
            model.setLastName(dto.lastName());
            model.setContactNumber(dto.contactNumber());
            model.setPositionTitle(dto.positionTitle());
            model.setProfileImageUrl(dto.profileImageUrl());
            model.setUpdatedBy(dto.updatedBy());
            model.setStatus(dto.status());
            model.setEmployeeStartDate(dto.employeeStartDate());
            model.setEmployeeEndDate(dto.employeeEndDate());
            model.setLastLoginAt(LocalDateTime.now());
            model.setCreatedAt(model.getCreatedAt());
            model.setUpdatedAt(LocalDateTime.now());
        return saveEmployee(model);
    }

    public Employee createNewEmployee(EmployeeDto dto) {
        var model = new Employee();
            model.setEmployeeId(sequenceGeneratorService.getNextEmployeeSequence("midnight_employee_seq"));
            model.setEmail(dto.email());
            model.setFirstName(dto.firstName());
            model.setLastName(dto.lastName());
            model.setContactNumber(dto.contactNumber());
            model.setPositionTitle(dto.positionTitle());
            model.setProfileImageUrl(dto.profileImageUrl());
            model.setCreatedBy(dto.createdBy());
            model.setUpdatedBy(dto.updatedBy());
            model.setStatus(dto.status());
            model.setEmployeeStartDate(LocalDate.now());
            model.setEmployeeEndDate(null);
            model.setLastLoginAt(LocalDateTime.now());
            model.setCreatedAt(LocalDateTime.now());
            model.setUpdatedAt(LocalDateTime.now());
        return employeeRepository.save(model);
    }
}
