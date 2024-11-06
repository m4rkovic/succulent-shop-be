package com.m4rkovic.succulent_shop.validator;

import com.m4rkovic.succulent_shop.dto.CategoryDTO;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CategoryValidator {

    public List<String> validate(CategoryDTO categoryDTO) {
        List<String> violations = new ArrayList<>();

        validateBasicFields(categoryDTO, violations);

        return violations;
    }

    private void validateBasicFields(CategoryDTO categoryDTO, List<String> violations) {
        if (StringUtils.isBlank(categoryDTO.getCategoryName())) {
            violations.add("Category name cannot be empty");
        } else if (categoryDTO.getCategoryName().length() < 2) {
            violations.add("Category name must be at least 2 characters long");
        } else if (categoryDTO.getCategoryName().length() > 100) {
            violations.add("Category name cannot exceed 100 characters");
        }

        if (StringUtils.isNotBlank(categoryDTO.getCategoryDesc())
                && categoryDTO.getCategoryDesc().length() > 500) {
            violations.add("Category description cannot exceed 500 characters");
        }
    }

    public void validateAndThrow(CategoryDTO categoryDTO) {
        List<String> violations = validate(categoryDTO);
        if (!violations.isEmpty()) {
            throw new InvalidDataException(String.join(", ", violations));
        }
    }
}