package dev.glabay.services;

import dev.glabay.dtos.UserCredentialsDto;
import dev.glabay.models.UserProfile;
import dev.glabay.repos.UserProfileRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@Service
public class RegistrationService {
    private final UserProfileRepository userProfileRepository;

    public RegistrationService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public boolean userExists(String email) {
        return userProfileRepository.findByEmailIgnoreCase(email)
            .isPresent();
    }

    public void registerUser(UserCredentialsDto dto) {
        var model = new UserProfile();
            model.setEmail(dto.email());
            model.setFirstName(dto.firstName());
            model.setLastName(dto.lastName());
            model.setEncryptedPassword(new BCryptPasswordEncoder().encode(dto.password()));
        userProfileRepository.save(model);
    }
}
