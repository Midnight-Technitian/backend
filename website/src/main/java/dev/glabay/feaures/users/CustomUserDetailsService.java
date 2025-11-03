package dev.glabay.feaures.users;

import dev.glabay.feaures.roles.UserRoleService;
import dev.glabay.logging.MidnightLogger;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
@NullMarked
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final Logger log = MidnightLogger.getLogger(CustomUserDetailsService.class);

    private final UserProfileRepository userProfileRepository;
    private final UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading UserDetails for email: {}", username);
        if (!userProfileRepository.existsByEmail(username)) {
            log.debug("No user found for for email: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
        var cachedUser = userProfileRepository.findByEmailIgnoreCase(username);
        if (cachedUser.isPresent()) {
            log.info("UserDetails for email: {}", cachedUser.get().getEmail());
            return cachedUser
                .map(userProfile -> {
                    var cachedRoles = userRoleService.findAllForEmail(userProfile.getEmail());
                    var authorities = cachedRoles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole()))
                        .toList();

                    return new org.springframework.security.core.userdetails.User(
                        userProfile.getEmail(),
                        userProfile.getEncryptedPassword(),
                        authorities
                    );
                }).get();

        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
