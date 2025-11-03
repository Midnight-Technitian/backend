package dev.glabay.feaures.roles;

import dev.glabay.logging.MidnightLogger;
import dev.glabay.models.roles.Role;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-26
 */
@Service
public class UserRoleService {
    private final Logger logger = MidnightLogger.getLogger(UserRoleService.class);

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public boolean addRoleToUser(String email, Role role) {
        if (userHasRole(email, role)) {
            logger.debug("User ({}) already has role: {}", email, role);
            return false;
        }
        var userRole = new UserRole();
            userRole.setEmail(email);
            userRole.setRole(role.name());
        userRoleRepository.save(userRole);
        return true;
    }

    public boolean userHasRole(String email, Role role) {
        var cachedRoles = findAllForEmail(email);
        if (cachedRoles.isEmpty())
            return false;
        return cachedRoles.stream()
            .anyMatch(r -> r.getRole().equals(role.name()));
    }

    public List<UserRole> findAllForEmail(String email) {
        return userRoleRepository.findByEmailIgnoreCase(email);
    }
}
