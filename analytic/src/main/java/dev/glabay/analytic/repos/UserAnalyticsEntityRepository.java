package dev.glabay.analytic.repos;

import dev.glabay.analytic.models.UserAnalyticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAnalyticsEntityRepository extends JpaRepository<UserAnalyticsEntity, Long> {
}