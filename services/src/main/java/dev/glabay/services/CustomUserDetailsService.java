package dev.glabay.services;

import dev.glabay.models.CustomUserDetails;
import dev.glabay.repos.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserProfileRepository userProfileRepository;

    @Override
    @NullUnmarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var cachedUser = userProfileRepository.findByEmailIgnoreCase(username);
        return cachedUser
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
