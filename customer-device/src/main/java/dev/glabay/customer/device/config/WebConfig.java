package dev.glabay.customer.device.config;

import dev.glabay.customer.device.security.ApiTokenAuthInterceptor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-24
 */
@NullMarked
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ApiTokenAuthInterceptor botTokenAuthInterceptor;

    public WebConfig(ApiTokenAuthInterceptor botTokenAuthInterceptor) {
        this.botTokenAuthInterceptor = botTokenAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(botTokenAuthInterceptor)
            .addPathPatterns("/api/**");
    }
}
