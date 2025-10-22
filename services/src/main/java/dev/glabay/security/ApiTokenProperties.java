package dev.glabay.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-09-09
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "api-token")
public class ApiTokenProperties {
    // Primary Security token
    private String secret;
    // Backup token in case we have a leak and need to reset the primary token
    private String nextSecret;
}
