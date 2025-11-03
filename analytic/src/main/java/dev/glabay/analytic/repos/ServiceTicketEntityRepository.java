package dev.glabay.analytic.repos;

import dev.glabay.analytic.models.ServiceTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceTicketEntityRepository extends JpaRepository<ServiceTicketEntity, Long> {
    Optional<ServiceTicketEntity> findByTicketId(String ticketId);
}