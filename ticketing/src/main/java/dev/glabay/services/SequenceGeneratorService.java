package dev.glabay.services;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public record SequenceGeneratorService(
    MongoOperations mongoOps
) {
    public String getNextSequence(String sequenceName) {
        var counter = mongoOps.findAndModify(
            query(where("id").is(sequenceName)),
            new Update().inc("mongoSeq", 1),
            options().returnNew(true).upsert(true),
            MidnightTicketSequence.class
        );
        return String.valueOf((counter != null) ? counter.getMongoSeq() : 1L);
    }

    public String getNextTicketSequence() {
        var counter = mongoOps.findAndModify(
            query(where("id").is("midnight_ticket_seq")),
            new Update().inc("mongoSeq", 1),
            options().returnNew(true).upsert(true),
            MidnightTicketSequence.class
        );
        var id = (counter != null) ? counter.getMongoSeq() : 1L;
        return "TICKET-%d".formatted(id);
    }

    public String getNextDeviceSequence() {
        var counter = mongoOps.findAndModify(
            query(where("id").is("midnight_device_seq")),
            new Update().inc("mongoSeq", 1),
            options().returnNew(true).upsert(true),
            MidnightTicketSequence.class
        );
        var id = (counter != null) ? counter.getMongoSeq() : 1L;
        return "DEVICE-%d".formatted(id);
    }

    public String getNextNoteSequence() {
        var counter = mongoOps.findAndModify(
            query(where("id").is("midnight_service_note_seq")),
            new Update().inc("mongoSeq", 1),
            options().returnNew(true).upsert(true),
            MidnightTicketSequence.class
        );
        var id = (counter != null) ? counter.getMongoSeq() : 1L;
        return "SERVICE-NOTE-%d".formatted(id);
    }
}
