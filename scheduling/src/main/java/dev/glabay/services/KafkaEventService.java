package dev.glabay.services;

import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.events.schedule.EmployeeClockedInEvent;
import dev.glabay.kafka.events.schedule.EmployeeClockedOutEvent;
import dev.glabay.logging.MidnightLogger;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
@Service
@RequiredArgsConstructor
public class KafkaEventService {
    private final Logger logger = MidnightLogger.getLogger(KafkaEventService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishClockInEvent(String employeeId, LocalDateTime time) {
        var event = new EmployeeClockedInEvent(employeeId, time);
        kafkaTemplate.send(KafkaTopics.TECHNICIAN_CLOCK_IN.getTopicName(), employeeId, event);
        logger.info("Employee Clocked In Event sent to Kafka {}", event);
    }

    public void publishClockOutEvent(String employeeId, LocalDateTime time, Duration duration) {
        var event = new EmployeeClockedOutEvent(employeeId, time, duration);
        kafkaTemplate.send(KafkaTopics.TECHNICIAN_CLOCK_OUT.getTopicName(), employeeId, event);
        logger.info("Employee Clocked Out Event sent to Kafka {}", event);
    }
}
