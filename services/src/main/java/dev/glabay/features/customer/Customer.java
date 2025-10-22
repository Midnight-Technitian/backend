package dev.glabay.features.customer;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
}
