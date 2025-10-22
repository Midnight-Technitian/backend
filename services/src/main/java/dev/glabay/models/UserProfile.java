package dev.glabay.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-30
 */
@Getter
@Setter
@Entity(name = "UserProfile")
@Table(name = "USER_PROFILE")
public class UserProfile {

    @Id
    @SequenceGenerator(
            name = "user_profile_sequence",
            sequenceName = "user_profile_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profile_sequence")
    @Column(name = "UID", updatable = false)
    private Long uid;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "CONTACT_NUMBER")
    private String contactNumber;

    @Column(name = "PASSWORD")
    private String encryptedPassword;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}
