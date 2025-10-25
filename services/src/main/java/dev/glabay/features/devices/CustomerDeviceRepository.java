package dev.glabay.features.devices;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerDeviceRepository extends JpaRepository<CustomerDevice, Long> {
    List<CustomerDevice> findByCustomerEmailIgnoreCase(String customerEmail);
}