package dev.glabay.scheduling.repos;

import dev.glabay.scheduling.models.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    Optional<Schedule> findByEmployeeIdAndWeekStartDate(String employeeId, LocalDate weekStartDate);
    List<Schedule> findByWeekStartDate(LocalDate weekStartDate);
}
