package dev.glabay.customer.repos;

import dev.glabay.customer.models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, Long> {
    Optional<Customer> findByEmailIgnoreCase(String email);
}