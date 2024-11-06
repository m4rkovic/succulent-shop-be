package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.UserDTO;
import com.m4rkovic.succulent_shop.exceptions.ValidationException;
import com.m4rkovic.succulent_shop.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final UserValidator userValidator;

    public void validateUserDTO(UserDTO userDTO) {
        if (userDTO == null) {
            throw new ValidationException("User data cannot be null");
        }

        List<String> violations = userValidator.validate(userDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException("User validation failed", violations);
        }
    }
}