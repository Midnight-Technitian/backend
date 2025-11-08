package dev.glabay.customer.device.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-11-08
 */
@Component
public class ApiTokenProperties {

    @Value( "${api.token.secret}")
    private String secret;

    public String getSecret() {
        return secret;
    }
}
