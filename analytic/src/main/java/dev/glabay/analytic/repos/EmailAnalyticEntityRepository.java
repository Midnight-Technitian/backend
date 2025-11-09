package dev.glabay.analytic.repos;

import dev.glabay.analytic.models.EmailAnalyticEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailAnalyticEntityRepository extends JpaRepository<EmailAnalyticEntity, Long> {
    Optional<EmailAnalyticEntity> findByTemplateNameIgnoreCase(String templateName);
}
