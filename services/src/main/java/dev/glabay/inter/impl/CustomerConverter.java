package dev.glabay.inter.impl;


import dev.glabay.dtos.CustomerDto;
import dev.glabay.inter.DtoConverter;
import dev.glabay.models.Customer;

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
            model.getContactNumber()
        );
    }
}
