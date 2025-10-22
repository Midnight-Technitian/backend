package dev.glabay.inter.impl;


import dev.glabay.dtos.UserProfileDto;
import dev.glabay.inter.DtoConverter;
import dev.glabay.models.UserProfile;

import java.time.LocalDateTime;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public interface UserProfileConverter extends DtoConverter<UserProfile, UserProfileDto> {
    @Override
    default UserProfileDto mapToDto(UserProfile model) {
        return new UserProfileDto(
            model.getEmail(),
            model.getFirstName(),
            model.getLastName(),
            model.getContactNumber(),
            model.getEncryptedPassword(),
            model.getCreatedAt(),
            LocalDateTime.now()
        );
    }
}
