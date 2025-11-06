package dev.glabay.scheduling.repos;

import dev.glabay.scheduling.models.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
}
