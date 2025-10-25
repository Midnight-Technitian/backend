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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_seq")
    @SequenceGenerator(name = "device_seq", sequenceName = "device_id_seq", allocationSize = 1)
    private Long deviceId;
    private String customerEmail;

    private String deviceName;
    private String deviceType;
    private String deviceInfo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
