package dev.glabay.service.repos;

import dev.glabay.service.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByServiceId(Long serviceId);
}