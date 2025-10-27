package dev.glabay.employees.models;

import dev.glabay.models.EmploymentStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-26
 */
@Getter
@Setter
@Document(collection = "employee")
public class Employee {
    @Id
    private String employeeId;

    private String email;
    private String firstName;
    private String lastName;
    private String contactNumber;

    private String positionTitle;
    private String profileImageUrl;

    private String createdBy;
    private String updatedBy;

    private EmploymentStatus status;

    private LocalDate employeeStartDate;
    private LocalDate employeeEndDate;

    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
