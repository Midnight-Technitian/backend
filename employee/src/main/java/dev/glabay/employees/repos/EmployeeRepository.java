package dev.glabay.employees.repos;

import dev.glabay.employees.models.Employee;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@NullMarked
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<Employee> findByEmailIgnoreCase(String email);
    Optional<Employee> findByEmployeeId(String employeeId);
}