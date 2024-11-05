package com.m4rkovic.succulent_shop.validator;

import com.m4rkovic.succulent_shop.dto.ProductDTO;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class ProductValidator {

    public List<String> validate(ProductDTO productDTO) {
        List<String> violations = new ArrayList<>();

        validateBasicFields(productDTO, violations);
        validateEnums(productDTO, violations);
        validatePotSpecificFields(productDTO, violations);
        validatePricing(productDTO, violations);

        return violations;
    }

    private void validateBasicFields(ProductDTO productDTO, List<String> violations) {
        if (StringUtils.isBlank(productDTO.getProductName())) {
            violations.add("Product name cannot be empty");
        }

        if (StringUtils.isBlank(productDTO.getProductDesc())) {
            violations.add("Product description cannot be empty");
        }
    }

    public void validateAndThrow(ProductDTO productDTO) {
        List<String> violations = validate(productDTO);
        if (!violations.isEmpty()) {
            throw new InvalidDataException(String.join(", ", violations));
        }
    }

    private void validateEnums(ProductDTO productDTO, List<String> violations) {

        if (StringUtils.isNotBlank(productDTO.getProductType())) {
            try {
                ProductType.valueOf(productDTO.getProductType().toUpperCase());
            } catch (IllegalArgumentException e) {
                violations.add("Invalid product type value");
            }
        }
    }

    private void validatePotSpecificFields(ProductDTO productDTO, List<String> violations) {
        if (productDTO.isPot()) {
            if (StringUtils.isBlank(productDTO.getPotSize())) {
                violations.add("Pot size must be specified for pot products");
            }
            if (StringUtils.isBlank(productDTO.getPotType())) {
                violations.add("Pot type must be specified for pot products");
            }
            if (productDTO.getPotNumber() <= 0) {
                violations.add("Pot number must be greater than 0 for pot products");
            }
        }
    }


    private void validatePricing(ProductDTO productDTO, List<String> violations) {
        if (productDTO.getPrice() == null) {
            violations.add("Price cannot be null");
        } else if (productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            violations.add("Price must be greater than zero");
        }
    }
}