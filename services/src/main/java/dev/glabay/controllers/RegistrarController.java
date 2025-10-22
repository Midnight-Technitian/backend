package dev.glabay.controllers;

import dev.glabay.dtos.UserCredentialsDto;
import dev.glabay.models.request.RegistrationStatus;
import dev.glabay.services.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Glabay | Glabay-Studios
 * @project backend
 * @social Discord: Glabay
 * @since 2025-10-21
 */
@RestController("/api/v1/registrar")
public class RegistrarController {
    private final RegistrationService registrationService;

    public RegistrarController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("")
    public ResponseEntity<RegistrationStatus> registerUser(@RequestBody UserCredentialsDto dto) {
        var exists = registrationService.userExists(dto.email());
        if (exists)
            return new ResponseEntity<>(RegistrationStatus.ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        if (!dto.password().equals(dto.rePassword()))
            return new ResponseEntity<>(RegistrationStatus.INVALID_CREDENTIALS, HttpStatus.BAD_REQUEST);
        registrationService.registerUser(dto);
        return new ResponseEntity<>(RegistrationStatus.CREATED, HttpStatus.CREATED);
    }
}
