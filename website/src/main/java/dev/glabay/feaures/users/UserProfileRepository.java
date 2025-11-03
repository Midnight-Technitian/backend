package dev.glabay.feaures.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    boolean existsByEmail(String email);

    Optional<UserProfile> findByEmailIgnoreCase(String email);
}