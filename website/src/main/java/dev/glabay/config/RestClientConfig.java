package dev.glabay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2025-10-17
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient getRestClient() {
        return RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
}
