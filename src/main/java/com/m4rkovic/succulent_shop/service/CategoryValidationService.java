package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.CategoryDTO;
import com.m4rkovic.succulent_shop.exceptions.ValidationException;
import com.m4rkovic.succulent_shop.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryValidationService {
    private final CategoryValidator categoryValidator;

    public void validateCategoryDTO(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            throw new ValidationException("Category data cannot be null");
        }

        List<String> violations = categoryValidator.validate(categoryDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException("Category validation failed ", violations);
        }
    }
}