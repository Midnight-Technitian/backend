package dev.glabay.employees.services;

import dev.glabay.dtos.EmployeeDto;
import dev.glabay.employees.models.Employee;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
*/
public interface EmployeeConverter {
    default EmployeeDto mapToDto(Employee model) {
        return new EmployeeDto(
            model.getEmployeeId(),
            model.getEmail(),
            model.getFirstName(),
            model.getLastName(),
            model.getContactNumber(),
            model.getPositionTitle(),
            model.getProfileImageUrl(),
            model.getCreatedBy(),
            model.getUpdatedBy(),
            model.getStatus(),
            model.getEmployeeStartDate(),
            model.getEmployeeEndDate(),
            model.getLastLoginAt(),
            model.getCreatedAt(),
            model.getUpdatedAt()
        );
    }
}
