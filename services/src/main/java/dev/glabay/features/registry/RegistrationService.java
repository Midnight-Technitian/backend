package dev.glabay.features.registry;

import dev.glabay.dtos.UserCredentialsDto;
import dev.glabay.features.user.UserProfile;
import dev.glabay.features.user.UserProfileRepository;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public RegistrationService(UserProfileRepository userProfileRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean userExists(String email) {
        return userProfileRepository.findByEmailIgnoreCase(email)
            .isPresent();
    }

    public UserProfile registeredUser(UserCredentialsDto dto) {
        var model = new UserProfile();
            model.setEmail(dto.email());
            model.setFirstName(dto.firstName());
            model.setLastName(dto.lastName());
            model.setEncryptedPassword(passwordEncoder.encode(dto.password()));
        return userProfileRepository.saveAndFlush(model);
    }
}
