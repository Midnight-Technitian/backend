package dev.glabay.analytic.repos;

import dev.glabay.analytic.models.EmailActivityFactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailActivityFactEntityRepository extends JpaRepository<EmailActivityFactEntity, Long> {
    Optional<EmailActivityFactEntity> findByEmailId(String emailId);
}
