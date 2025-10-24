package dev.glabay.features.service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Glaba
 * @project GlabTech
 * @social Discord: Glabay | GitHub: github.com/glabay
 * @since 2024-02-18
 */
@Getter
@Setter
@Entity(name = "services")
@Table(name = "SERVICES")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long serviceId;
   private String serviceName;
   private String serviceDescription;
   private double servicePrice;
   private boolean fixedRate;
};
