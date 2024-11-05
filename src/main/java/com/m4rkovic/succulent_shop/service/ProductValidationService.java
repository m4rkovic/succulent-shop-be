package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.ProductDTO;
import com.m4rkovic.succulent_shop.exceptions.ValidationException;
import com.m4rkovic.succulent_shop.validator.ProductValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Validation Service
@Service
@RequiredArgsConstructor
public class ProductValidationService {
    private final ProductValidator productValidator;

    public void validateProductDTO(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new ValidationException("Product data cannot be null");
        }

        List<String> violations = productValidator.validate(productDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException("Product validation failed ", violations);
        }
    }
}
