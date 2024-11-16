package com.m4rkovic.succulent_shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Products list cannot be empty")
    private List<Long> productsIds;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Delivery method is required")
    private String deliveryMethod;
}
