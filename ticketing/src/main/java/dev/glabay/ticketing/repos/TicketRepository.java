package dev.glabay.ticketing.repos;

import dev.glabay.ticketing.models.ServiceTicket;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-22
 */
@NullMarked
public interface TicketRepository extends MongoRepository<ServiceTicket, String> {
    Collection<ServiceTicket> findAllByCustomerIdAndStatus(String customerId, String status);
    Collection<ServiceTicket> findAllByCustomerId(String customerId);
    Collection<ServiceTicket> findAllByStatus(String status);
    Collection<ServiceTicket> findAllByCustomerDeviceId(String customerDeviceId);
    Collection<ServiceTicket> findAllByEmployeeId(String employeeId);
    Collection<ServiceTicket> findAllByServiceId(String serviceId);
}
