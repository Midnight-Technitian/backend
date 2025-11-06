package dev.glabay.scheduling.repos;

import dev.glabay.scheduling.models.TimeRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
public interface TimeRecordRepository extends MongoRepository<TimeRecord, String> {

    @Query("{ 'employeeId': ?0, 'clockOut': null }")
    Optional<TimeRecord> findOpenRecordByEmployeeId(String employeeId);

    List<TimeRecord> findAllByEmployeeIdAndDateBetween(String employeeId, LocalDate start, LocalDate end);
}
