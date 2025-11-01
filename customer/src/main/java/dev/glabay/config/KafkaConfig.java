package dev.glabay.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-31
 */
@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic userEventsTopic() {
        return new NewTopic("user.events", 1, (short) 1);
    }
}
