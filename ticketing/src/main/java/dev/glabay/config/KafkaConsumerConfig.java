package dev.glabay.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-11-01
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaHost;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        var deserializer = new JsonDeserializer<>();
            deserializer.addTrustedPackages("*");

        var config = new HashMap<String, Object>();
            config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
            config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
            config.put(ConsumerConfig.GROUP_ID_CONFIG, "ticketing-service");

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
