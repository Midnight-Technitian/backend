package dev.glabay.scheduling.models;

import dev.glabay.dtos.Shift;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
@Document(collection = "schedules")
public record Schedule(
    @Id
    String id,
    String employeeId,
    LocalDate weekStartDate,
    List<Shift> shifts
) {}
