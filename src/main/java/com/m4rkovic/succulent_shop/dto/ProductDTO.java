package com.m4rkovic.succulent_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotNull(message = "Plant ID is required")
    private Long plantId;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String productName;

    @Size(max = 1000, message = "Product description cannot exceed 1000 characters")
    private String productDesc;

    @Pattern
            (regexp = "^(SMALL|MEDIUM|LARGE|EXTRA_LARGE)$", message = "Invalid pot size")
    private String potSize;

    @NotBlank(message = "Product type is required")
    @Pattern(regexp = "^(PLANT|POT|TOOL)$", message = "Invalid product type")
    private String productType;

    private boolean isPot;

    @Min(value = 0, message = "Pot number cannot be negative")
    private int potNumber;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Digits(integer = 6, fraction = 2, message = "Price must have at most 6 digits and 2 decimal places")
    private BigDecimal price;

    @Pattern(regexp = "^(CERAMIC|PLASTIC|TERRACOTTA)$", message = "Invalid pot type")
    private String potType;

    @Pattern(regexp = "^(PRUNER|SHOVEL|RAKE|WATERCAN)$", message = "Invalid tool type")
    private String toolType;
}
