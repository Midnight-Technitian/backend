package dev.glabay.customer.device.services;

import dev.glabay.customer.device.models.MidnightDeviceSequence;
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
    public Long getNextCustomerSequence(String sequenceName) {
        var counter = mongoOps.findAndModify(
            query(where("id").is(sequenceName)),
            new Update().inc("mongoSeq", 1),
            options().returnNew(true).upsert(true),
            MidnightDeviceSequence.class
        );
        return (counter != null) ? counter.getMongoSeq() : 1L;
    }
}
