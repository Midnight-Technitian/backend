package dev.glabay.features.devices;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "DROP_OFFS")
public class CustomerDevice {
    @Id
    private Integer dropOffId;
    private Integer customerId;
    private Date dropOffDate;
    private String notes;
}
