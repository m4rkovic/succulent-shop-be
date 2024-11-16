package com.m4rkovic.succulent_shop.mapper;

import com.m4rkovic.succulent_shop.dto.OrderDTO;
import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.service.ProductService;
import com.m4rkovic.succulent_shop.service.UserService;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderMapper {

    private final UserService userService;
    private final ProductService productService;

    public OrderMapper(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    public Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = userService.findById(dto.getUserId());
        List<Product> products = dto.getProductsIds() != null ?
                productService.findProductsByIds(dto.getProductsIds()) : Collections.emptyList();

        BigDecimal orderTotal = products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Order.builder()
                .user(user)
                .address(dto.getAddress())
                .deliveryMethod(dto.getDeliveryMethod())
                .orderTotal(orderTotal)
                .products(products)
                .build();
    }

    public OrderDTO toDTO(Order entity) {
        if (entity == null) {
            return null;
        }

        return OrderDTO.builder()
                .userId(entity.getUser().getId())
                .productsIds(entity.getProducts() != null ?
                        entity.getProducts().stream().map(Product::getId).collect(Collectors.toList()) : Collections.emptyList())
                .address(entity.getAddress())
                .deliveryMethod(entity.getDeliveryMethod())
                .build();
    }

    public void updateEntityFromDTO(Order entity, OrderDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        if (dto.getDeliveryMethod() != null) {
            entity.setDeliveryMethod(dto.getDeliveryMethod());
        }
        if (dto.getProductsIds() != null && !dto.getProductsIds().isEmpty()) {
            List<Product> products = productService.findProductsByIds(dto.getProductsIds());
            entity.setProducts(products);
            // Update order total when products change
            BigDecimal orderTotal = products.stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            entity.setOrderTotal(orderTotal);
        }
    }

    public List<OrderDTO> toDTOList(List<Order> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
