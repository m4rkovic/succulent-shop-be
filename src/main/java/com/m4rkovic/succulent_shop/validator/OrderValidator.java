package com.m4rkovic.succulent_shop.validator;

import com.m4rkovic.succulent_shop.dto.OrderDTO;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderValidator {

    public List<String> validate(OrderDTO orderDTO) {
        List<String> violations = new ArrayList<>();

        validateBasicFields(orderDTO, violations);
        validateAddress(orderDTO, violations);
        validateProducts(orderDTO, violations);
        validateDeliveryMethod(orderDTO, violations);

        return violations;
    }

    private void validateBasicFields(OrderDTO orderDTO, List<String> violations) {
        if (orderDTO.getUserId() == null) {
            violations.add("User ID cannot be null");
        }

//        if (orderDTO.getOrderDate() == null || orderDTO.getOrderDate().after(new Date())) {
//            violations.add("Order date cannot be in the future or null");
//        }
//
//        if (StringUtils.isBlank(orderDTO.getOrderCode())) {
//            violations.add("Order code cannot be empty");
//        }
    }

    private void validateAddress(OrderDTO orderDTO, List<String> violations) {
        if (StringUtils.isBlank(orderDTO.getAddress())) {
            violations.add("Address cannot be empty");
        }
    }

    private void validateProducts(OrderDTO orderDTO, List<String> violations) {
        if (orderDTO.getProductsIds() == null || orderDTO.getProductsIds().isEmpty()) {
            violations.add("Order must contain at least one product");
        }
    }

    private void validateDeliveryMethod(OrderDTO orderDTO, List<String> violations) {
        if (StringUtils.isBlank(orderDTO.getDeliveryMethod())) {
            violations.add("Delivery method cannot be empty");
        }
    }
    public void validateAndThrow(OrderDTO orderDTO) {
        List<String> violations = validate(orderDTO);
        if (!violations.isEmpty()) {
            throw new InvalidDataException(String.join(", ", violations));
        }
    }
}