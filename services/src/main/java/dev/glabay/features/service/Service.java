package dev.glabay.features.service;

import jakarta.persistence.*;

/**
 * @author Glaba
 * @project GlabTech
 * @social Discord: Glabay | GitHub: github.com/glabay
 * @since 2024-02-18
 */
@Entity(name = "services")
@Table(name = "SERVICES")
public record Service(
    @Id
    @SequenceGenerator(
        name = "services_sequence",
        sequenceName = "services_sequence",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_sequence")
    String serviceName,
    String serviceDescription,
    double servicePrice,
    boolean fixedRate
) {}
