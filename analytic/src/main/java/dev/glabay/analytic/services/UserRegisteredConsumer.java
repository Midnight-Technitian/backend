package dev.glabay.analytic.services;

import dev.glabay.analytic.models.UserAnalyticsEntity;
import dev.glabay.analytic.repos.UserAnalyticsEntityRepository;
import dev.glabay.kafka.events.user.UserRegisteredEvent;
import dev.glabay.logging.MidnightLogger;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-11-02
 */
@Service
@NullMarked
public class UserRegisteredConsumer {
    private final Logger logger = new MidnightLogger(UserRegisteredConsumer.class);

    private final UserAnalyticsEntityRepository userAnalyticsEntityRepository;

    public UserRegisteredConsumer(UserAnalyticsEntityRepository userAnalyticsEntityRepository) {
        this.userAnalyticsEntityRepository = userAnalyticsEntityRepository;
    }

    @KafkaListener(topics = "user-registered", groupId = "analytic-service")
    public void handleUserRegistered(UserRegisteredEvent event) {
        logger.info("Received event: {}", event);
        var userDto = event.userDto();
        var record = new UserAnalyticsEntity();
            record.setUserId(userDto.uid());
            record.setEmail(userDto.email());
            record.setIpAddress(event.ipAddress());
            record.setRegisteredAt(LocalDateTime.now());
        var cachedRecord = userAnalyticsEntityRepository.save(record);
        logger.info("User registered: {}", cachedRecord);
    }
}
