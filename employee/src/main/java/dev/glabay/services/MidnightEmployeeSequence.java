package dev.glabay.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("midnight_employee_sequences")
public class MidnightEmployeeSequence {
    private String id;
    private Long mongoSeq;
}
