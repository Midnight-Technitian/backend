package dev.glabay.scheduling.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * @author Glabay | Glabay-Studios
 * @project midnight-technician
 * @social Discord: Glabay
 * @since 2025-11-05
 */
public record Shift(
    DayOfWeek day,
    LocalTime startTime,
    LocalTime endTime
) {}
