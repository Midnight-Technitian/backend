package dev.glabay.customer.models;

import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Document("midnight_customer_sequences")
public class MidnightCustomerSequence {
    private String id;
    private Long mongoSeq;

    public String getId() {
        return id;
    }

    public Long getMongoSeq() {
        return mongoSeq;
    }
}
