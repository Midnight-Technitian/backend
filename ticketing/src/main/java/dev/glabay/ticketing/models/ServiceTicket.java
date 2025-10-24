package dev.glabay.ticketing.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-22
 */
@Getter
@Setter
@Document(collection = "service_ticket")
public class ServiceTicket {
    @Id
    private String ticketId;

    private String status;
    private String title;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String customerId;
    private String customerDeviceId;
    private String employeeId;
    private String serviceId;

    private List<ServiceNote> notes;
}
