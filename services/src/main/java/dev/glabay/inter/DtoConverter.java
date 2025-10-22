package dev.glabay.inter;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
*/
public interface DtoConverter<E, D> {
    D mapToDto(E model);
}
