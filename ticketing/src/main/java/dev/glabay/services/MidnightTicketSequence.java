package dev.glabay.services;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("midnight_ticket_sequences")
public class MidnightTicketSequence {
    private String id;
    private Long mongoSeq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMongoSeq() {
        return mongoSeq;
    }

    public void setMongoSeq(Long mongoSeq) {
        this.mongoSeq = mongoSeq;
    }
}
