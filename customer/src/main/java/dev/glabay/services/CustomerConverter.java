package dev.glabay.services;


import dev.glabay.customer.models.Customer;
import dev.glabay.dtos.CustomerDto;
import dev.glabay.inter.DtoConverter;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
 */
public interface CustomerConverter extends DtoConverter<Customer, CustomerDto> {

    @Override
    default CustomerDto mapToDto(Customer model) {
        return new CustomerDto(
            model.getFirstName(),
            model.getLastName(),
            model.getEmail(),
            model.getContactNumber(),
            model.getCreatedAt(),
            model.getUpdatedAt()
        );
    }
}
