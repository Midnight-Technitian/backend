package dev.glabay.customer.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("midnight_customer_sequences")
public class MidnightCustomerSequence {
    private String id;
    private Long mongoSeq;
}
