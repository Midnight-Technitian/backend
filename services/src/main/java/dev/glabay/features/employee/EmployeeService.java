package dev.glabay.features.employee;

import dev.glabay.dtos.EmployeeDto;
import dev.glabay.inter.impl.EmployeeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Glabay | Glabay-Studios
 * @project GlabTech
 * @social Discord: Glabay
 * @since 2024-11-22
*/
@Service
@RequiredArgsConstructor
public class EmployeeService implements EmployeeConverter {

    private final EmployeeRepository employeeRepository;

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
            .stream()
            .map(this::mapToDto)
            .toList();
    }

}
