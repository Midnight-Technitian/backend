package dev.glabay.ticketing.models;

import dev.glabay.dtos.ServiceTicketDto;
import dev.glabay.models.ServiceNote;
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
    private String id;

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

    public ServiceTicketDto mapToDto() {
        var dto = new ServiceTicketDto();
            dto.setTicketId(this.ticketId);
            dto.setStatus(this.status);
            dto.setTitle(this.title);
            dto.setDescription(this.description);
            dto.setCustomerId(this.customerId);
            dto.setCustomerDeviceId(this.customerDeviceId);
            dto.setEmployeeId(this.employeeId);
            dto.setServiceId(this.serviceId);
            dto.setNotes(this.notes);
            dto.setCreatedAt(this.createdAt);
            dto.setUpdatedAt(this.updatedAt);
        return dto;
    }
}
