package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.OrderDTO;
import com.m4rkovic.succulent_shop.exceptions.ValidationException;
import com.m4rkovic.succulent_shop.validator.OrderValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderValidationService {

    private final OrderValidator orderValidator;

    public void validateOrderDTO(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new ValidationException("Order data cannot be null");
        }

        List<String> violations = orderValidator.validate(orderDTO);
        if (!violations.isEmpty()) {
            throw new ValidationException("Order validation failed ", violations);
        }
    }
}