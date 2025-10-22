package dev.glabay.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Glaba
 * @project GlabTech
 * @social Discord: Glabay | GitHub: github.com/glabay
 * @since 2024-02-18
 */
@Getter
@Setter
@Entity(name = "employee")
@Table(name = "EMPLOYEES")
public class Employee {
    @Id
    private String employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private LocalDate employeeStartDate;
    private LocalDate employeeEndDate;
    private String contactNumber;
    private String position;
}
