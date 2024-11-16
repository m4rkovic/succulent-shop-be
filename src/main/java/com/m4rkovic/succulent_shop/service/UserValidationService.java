package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.UserDTO;
import com.m4rkovic.succulent_shop.exceptions.ValidationException;
import com.m4rkovic.succulent_shop.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    private final UserValidator userValidator;

    /**
     * Validates user data for either creation or update
     *
     * @param userDTO  The user data to validate
     * @param isUpdate Flag indicating if this is an update operation
     * @throws ValidationException if validation fails
     */
    public void validateUserDTO(UserDTO userDTO, boolean isUpdate) {
        if (userDTO == null) {
            throw new ValidationException("User data cannot be null");
        }

        List<String> violations = userValidator.validate(userDTO, isUpdate);
        if (!violations.isEmpty()) {
            log.debug("Validation failed for user DTO: {}. Violations: {}", userDTO, violations);
            throw new ValidationException("User validation failed", violations);
        }
    }

    /**
     * Validates user data for creation
     *
     * @param userDTO The user data to validate
     * @throws ValidationException if validation fails
     */
    public void validateUserDTO(UserDTO userDTO) {
        validateUserDTO(userDTO, false);
    }
}