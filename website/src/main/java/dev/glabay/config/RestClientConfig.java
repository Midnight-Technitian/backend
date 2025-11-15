package dev.glabay.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

    @Value( "${backend.api.token}")
    private String spiToken;

    @Bean
    @Primary
    public RestClient.Builder defaultRestClientBuilder() {
        return RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory());
    }

    @Bean("lbRestClientBuilder")
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-API-Key", spiToken);
    }

    @Bean
    public RestClient restClient(@Qualifier("lbRestClientBuilder") RestClient.Builder builder) {
        return builder.build();
    }
}
