package dev.glabay.services;

import dev.glabay.dtos.UserCredentialsDto;
import dev.glabay.dtos.UserProfileDto;
import dev.glabay.feaures.users.UserProfile;
import dev.glabay.feaures.users.UserProfileRepository;
import dev.glabay.kafka.KafkaTopics;
import dev.glabay.kafka.events.UserRegisteredEvent;
import dev.glabay.models.request.RegistrationStatus;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@Service
@NullMarked
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegistrationStatus registerUser(UserCredentialsDto request, String ipAddress) {
        var exists = userProfileRepository.existsByEmail(request.email());
        if (exists)
            return RegistrationStatus.ALREADY_EXISTS;

        if (request.password().isEmpty() || request.password().isBlank() ||
            request.rePassword().isEmpty() || request.rePassword().isBlank() ||
            !request.password().equals(request.rePassword())
        ) return RegistrationStatus.INVALID_CREDENTIALS;

        var newUser = new UserProfile();
            newUser.setEmail(request.email());
            newUser.setFirstName(request.firstName());
            newUser.setLastName(request.lastName());
            newUser.setContactNumber(request.contactNumber());
            newUser.setEncryptedPassword(passwordEncoder.encode(request.password()));
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());

        var cached = userProfileRepository.save(newUser);
        logger.info("User created and saved {}", cached);
        if (cached == null)
            return RegistrationStatus.FAILED;
        createNewCustomer(cached, ipAddress);
        return RegistrationStatus.CREATED;
    }

    private void createNewCustomer(UserProfile newUser, String ipAddress) {
        logger.info("Creating new customer for user {}", newUser);
        var dto = new UserProfileDto(
            newUser.getUid(),
            newUser.getEmail(),
            newUser.getFirstName(),
            newUser.getLastName(),
            newUser.getContactNumber(),
            newUser.getCreatedAt(),
            newUser.getUpdatedAt()
        );
        // Create an event to make a new customer
        var event = new UserRegisteredEvent(dto, ipAddress);
        kafkaTemplate.send(KafkaTopics.USER_REGISTRATION.getTopicName(), dto.email(), event);
        logger.info("User Registered Event sent to Kafka {}", event);
    }
}
