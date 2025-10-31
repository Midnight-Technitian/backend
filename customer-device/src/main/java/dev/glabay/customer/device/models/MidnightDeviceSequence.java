package dev.glabay.customer.device.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("midnight_customer_sequences")
public class MidnightDeviceSequence {
    private String id;
    private Long mongoSeq;
}
