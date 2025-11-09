package dev.glabay.analytic.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Dimension table entity for email templates (dim_emails).
 */
@Entity
@Table(name = "dim_emails")
public class EmailAnalyticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "template_name", nullable = false, unique = true, length = 100)
    private String templateName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "EmailAnalyticEntity{" +
            "id=" + id +
            ", templateName='" + templateName + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
