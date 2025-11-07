package dev.glabay.config;

import dev.glabay.kafka.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic emailSendRequestTopic() {
        return TopicBuilder.name(KafkaTopics.EMAIL_SEND_REQUEST.getTopicName())
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic emailSentAnalyticTopic() {
        return TopicBuilder.name(KafkaTopics.EMAIL_SENT_ANALYTIC.getTopicName())
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic emailSendRequestDltTopic() {
        return TopicBuilder.name(KafkaTopics.EMAIL_SEND_REQUEST.getTopicName() + ".DLT")
            .partitions(3)
            .replicas(1)
            .build();
    }
}
