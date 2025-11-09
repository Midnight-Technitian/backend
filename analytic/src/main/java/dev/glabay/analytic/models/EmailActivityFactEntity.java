package dev.glabay.analytic.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Fact table entity for email activity (fact_email_activity).
 */
@Entity
@Table(
    name = "fact_email_activity",
    uniqueConstraints = {@UniqueConstraint(name = "uk_fact_email_email_id", columnNames = {"email_id"})},
    indexes = {
        @Index(name = "idx_fact_email_template_id", columnList = "template_id"),
        @Index(name = "idx_fact_email_status", columnList = "status")
    }
)
public class EmailActivityFactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "email_id", nullable = false, length = 100)
    private String emailId;

    @Column(name = "recipient", length = 255)
    private String recipient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private EmailAnalyticEntity template;

    @Column(name = "triggered_by", length = 100)
    private String triggeredBy;

    @Column(name = "service_origin", length = 100)
    private String serviceOrigin;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "latency_ms")
    private Long latencyMs;

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

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public EmailAnalyticEntity getTemplate() {
        return template;
    }

    public void setTemplate(EmailAnalyticEntity template) {
        this.template = template;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getServiceOrigin() {
        return serviceOrigin;
    }

    public void setServiceOrigin(String serviceOrigin) {
        this.serviceOrigin = serviceOrigin;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
