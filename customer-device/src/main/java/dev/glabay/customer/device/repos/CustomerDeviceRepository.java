package dev.glabay.customer.device.repos;

import dev.glabay.customer.device.models.CustomerDevice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerDeviceRepository extends MongoRepository<CustomerDevice, Long> {
    List<CustomerDevice> findByCustomerEmailIgnoreCase(String customerEmail);
}