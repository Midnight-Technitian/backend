package dev.glabay.features.customer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Glaba
 * @project GlabTech
 * @social Discord: Glabay | GitHub: github.com/glabay
 * @since 2024-02-18
 */
@Getter
@Setter
@Entity(name = "customer")
@Table(name = "CUSTOMERS")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
