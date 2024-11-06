package com.m4rkovic.succulent_shop.validator;

import com.m4rkovic.succulent_shop.dto.UserDTO;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j

public class UserValidator {

    public List<String> validate(UserDTO userDTO) {
        List<String> violations = new ArrayList<>();

        validateBasicFields(userDTO, violations);
        validateEmail(userDTO, violations);
        validatePassword(userDTO, violations);
        validateRole(userDTO, violations);

        return violations;
    }

    private void validateBasicFields(UserDTO userDTO, List<String> violations) {
        if (StringUtils.isBlank(userDTO.getFirstname())) {
            violations.add("First name cannot be empty");
        }

        if (StringUtils.isBlank(userDTO.getLastname())) {
            violations.add("Last name cannot be empty");
        }

        if (StringUtils.isBlank(userDTO.getAddress())) {
            violations.add("Address cannot be empty");
        }
    }

    private void validateEmail(UserDTO userDTO, List<String> violations) {
        if (StringUtils.isBlank(userDTO.getEmail())) {
            violations.add("Email cannot be empty");
        } else if (!isValidEmail(userDTO.getEmail())) {
            violations.add("Email format is invalid");
        }
    }

    private boolean isValidEmail(String email) {
        // Simple email regex, consider using a more robust one or a library for production use
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private void validatePassword(UserDTO userDTO, List<String> violations) {
        if (StringUtils.isBlank(userDTO.getPassword())) {
            violations.add("Password cannot be empty");
        } else if (userDTO.getPassword().length() < 6) {
            violations.add("Password must be at least 6 characters long");
        }
    }

    private void validateRole(UserDTO userDTO, List<String> violations) {
        if (userDTO.getRole() == null) {
            violations.add("Role must be specified");
        }
    }

    public void validateAndThrow(UserDTO userDTO) {
        List<String> violations = validate(userDTO);
        if (!violations.isEmpty()) {
            throw new InvalidDataException(String.join(", ", violations));
        }
    }
}
