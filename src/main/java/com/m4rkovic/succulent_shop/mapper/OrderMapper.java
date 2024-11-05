package com.m4rkovic.succulent_shop.mapper;

import com.m4rkovic.succulent_shop.dto.OrderDTO;
import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.service.ProductService;
import com.m4rkovic.succulent_shop.service.UserService;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
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

        return Order.builder()
                .orderDate(dto.getOrderDate())
                .orderStatus(dto.getOrderStatus())
                .user(user)
                .orderCode(dto.getOrderCode())
                .address(dto.getAddress())
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
                .orderCode(entity.getOrderCode())
                .orderDate(entity.getOrderDate())
                .orderStatus(entity.getOrderStatus())
                .address(entity.getAddress())
                .build();
    }

    public void updateEntityFromDTO(Order entity, OrderDTO dto) {
        if (entity == null || dto == null) {
            return;
        }

        if (dto.getOrderDate() != null) {
            entity.setOrderDate(dto.getOrderDate());
        }
        if (dto.getOrderStatus() != null) {
            entity.setOrderStatus(dto.getOrderStatus());
        }
        if (dto.getOrderCode() != null) {
            entity.setOrderCode(dto.getOrderCode());
        }
        if (dto.getAddress() != null) {
            entity.setAddress(dto.getAddress());
        }
        if (dto.getProductsIds() != null && !dto.getProductsIds().isEmpty()) {
            List<Product> products = productService.findProductsByIds(dto.getProductsIds());
            entity.setProducts(products);
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
