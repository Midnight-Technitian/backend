package dev.glabay.ticketing.models;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-22
 */
public record ServiceNote(
    String noteId,
    String ticketId,
    String technicianId,
    String title,
    String message
) {}
