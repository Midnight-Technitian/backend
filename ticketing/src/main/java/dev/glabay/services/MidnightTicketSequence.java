package dev.glabay.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("midnight_ticket_sequences")
public class MidnightTicketSequence {
    private String id;
    private Long mongoSeq;
}
