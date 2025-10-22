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
public class LoginService {
    private final UserProfileRepository userProfileRepository;

    public LoginService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public boolean userExists(String email) {
        return userProfileRepository.findByEmailIgnoreCase(email)
            .isPresent();
    }

    public UserProfile loginUser(UserCredentialsDto dto) {
        return userProfileRepository.findByEmailIgnoreCase(dto.email())
            .filter(userProfile -> new BCryptPasswordEncoder().matches(dto.password(), userProfile.getEncryptedPassword()))
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    }

}
