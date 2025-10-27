package dev.glabay.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-24
 */
@Configuration
public class MongoConfig {

    @Value("${database.port}")
    private int port;

    @Value("${database.name}")
    private String collection;

    @Value("${database.host}")
    private String host;

    @Value("${database.user}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Bean
    public MongoClient mongoClient() {
        var connString = new ConnectionString(
            "mongodb://%s:%s@%s:%d/%s?authSource=admin"
                .formatted(username, password, host, port, collection)
        );
        var settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, collection);
    }
}
