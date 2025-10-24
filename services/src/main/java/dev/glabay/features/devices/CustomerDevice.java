package dev.glabay.features.devices;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Glaba
 * @project GlabTech
 * @social Discord: Glabay | GitHub: github.com/glabay
 * @since 2024-02-18
 */
@Getter
@Setter
@Entity(name = "customer_device")
@Table(name = "CUSTOMER_DEVICE")
public class CustomerDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;
    private Long customerId;

    private String deviceName;
    private String deviceType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
