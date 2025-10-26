package dev.glabay;

import dev.glabay.logging.SentryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Ticketing {

    static void main(String[] args) {
        SentryConfig.initialize();
        SpringApplication.run(Ticketing.class, args);
    }

}
