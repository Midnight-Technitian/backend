package dev.glabay.feaures.roles;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Glabay | Glabay-Studios
 * @project frontend
 * @social Discord: Glabay
 * @since 2025-10-26
 */
@Getter
@Setter
@Entity(name = "userRole")
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", updatable = false)
    private Long uid;
    private String email;
    private String role;
}
