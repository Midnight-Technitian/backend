package dev.glabay.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("MidnightTicketSequence")
public class MidnightTicketSequence {
    private String id;
    private Long mongoSqq;
}
