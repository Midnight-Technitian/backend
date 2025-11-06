package dev.glabay.scheduling.services;

import dev.glabay.logging.MidnightLogger;
import dev.glabay.scheduling.models.TimeRecord;
import dev.glabay.scheduling.repos.TimeRecordRepository;
import dev.glabay.services.KafkaEventService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
@Service
@RequiredArgsConstructor
public class TimeTrackingService {
    private final Logger logger = MidnightLogger.getLogger(TimeTrackingService.class);

    private final KafkaEventService kafkaEventService;
    private final TimeRecordRepository timeRecordRepository;

    public boolean clockIn(String employeeId) {
        var openRecord = timeRecordRepository.findOpenRecordByEmployeeId(employeeId);
        if (openRecord.isPresent()) {
            logger.warn("Employee is already clocked in {}", openRecord.get());
            return false;
        }

        var record = new TimeRecord();
            record.setEmployeeId(employeeId);
            record.setDate(LocalDate.now());
            record.setClockIn(LocalDateTime.now());
        var cachedRecord = timeRecordRepository.save(record);
        // publish a clock-in event for analytic service
        kafkaEventService.publishClockInEvent(employeeId, LocalDateTime.now());
        logger.info("Employee clocked in successfully {}", cachedRecord);
        return true;
    }

    public boolean clockOut(String employeeId) {
        var optionalTimeRecord = timeRecordRepository.findOpenRecordByEmployeeId(employeeId);
        if (optionalTimeRecord.isEmpty()) {
            logger.warn("Employee is not clocked in {}", employeeId);
            return false;
        }
        var record = optionalTimeRecord.get();
            record.setClockOut(LocalDateTime.now());
            record.calculateTotalWorked();
        var cachedRecord = timeRecordRepository.save(record);
        // publish a clock-out event for analytic service
        kafkaEventService.publishClockOutEvent(employeeId, LocalDateTime.now(), Duration.between(record.getClockIn(), LocalDateTime.now()));
        logger.info("Employee clocked out successfully {}", cachedRecord);
        return true;
    }

}
