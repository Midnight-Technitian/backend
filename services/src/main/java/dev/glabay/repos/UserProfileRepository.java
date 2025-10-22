package dev.glabay.repos;

import dev.glabay.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-30
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmailIgnoreCase(String email);
    boolean existsByFirstNameIgnoreCase(String username);
}