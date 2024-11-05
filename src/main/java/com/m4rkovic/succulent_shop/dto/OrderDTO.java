package com.m4rkovic.succulent_shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

    @JsonProperty("productsIds")
    @NotNull(message = "Product IDs list is required")
    private List<Long> productsIds;

    @NotBlank(message = "Order code is required")
    @Size(min = 5, max = 20, message = "Order code must be between 5 and 20 characters")
    private String orderCode;

    @NotNull(message = "Order date is required")
    private Date orderDate;

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;

    @NotNull(message = "Address is required")
    private AddressDTO address;
}
