package dev.glabay.config;

import dev.glabay.kafka.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-11-01
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(KafkaTopics.USER_REGISTRATION.getTopicName())
            .partitions(1)
            .replicas(1)
            .build();
    }
}
