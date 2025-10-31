package dev.glabay.customer.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document("customer")
public class Customer {
    @Id
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
