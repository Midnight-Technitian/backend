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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerDeviceId() {
        return customerDeviceId;
    }

    public void setCustomerDeviceId(String customerDeviceId) {
        this.customerDeviceId = customerDeviceId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<ServiceNote> getNotes() {
        return notes;
    }

    public void setNotes(List<ServiceNote> notes) {
        this.notes = notes;
    }

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
