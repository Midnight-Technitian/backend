package dev.glabay.customer.device.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author Glaba
 * @project GlabTech
 * @social Discord: Glabay | GitHub: github.com/glabay
 * @since 2024-02-18
 */
@Getter
@Setter
@Document("customer_device")
public class CustomerDevice {
    @Id
    private Long deviceId;
    private String customerEmail;

    private String deviceName;
    private String deviceType;
    private String deviceInfo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
