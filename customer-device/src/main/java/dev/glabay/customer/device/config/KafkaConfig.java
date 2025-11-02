package dev.glabay.customer.device.config;

import dev.glabay.kafka.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

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
        return TopicBuilder.name(KafkaTopics.CUSTOMER_DEVICE_REGISTRATION.getTopicName())
            .partitions(1)
            .replicas(1)
            .build();
    }
}
