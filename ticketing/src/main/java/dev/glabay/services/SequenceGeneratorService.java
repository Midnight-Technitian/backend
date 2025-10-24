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
}
