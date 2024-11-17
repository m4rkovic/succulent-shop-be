package com.m4rkovic.succulent_shop.response;

import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.enumerator.DeliveryMethod;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponse {
    private Long id;
    private Date orderDate;
    private Date orderUpdateDate;
    private OrderStatus orderStatus;
    private String orderCode;
    private String orderUpdateLog;
    private String address;
    private DeliveryMethod deliveryMethod;
    private BigDecimal subtotal;
    private BigDecimal deliveryCost;
    private BigDecimal orderTotal;
    private UserResponse user;
    private List<ProductResponse> products;

    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setOrderUpdateDate(order.getOrderUpdateDate());
        response.setOrderStatus(order.getOrderStatus());
        response.setOrderCode(order.getOrderCode());
        response.setOrderUpdateLog(order.getOrderUpdateLog());
        response.setAddress(order.getAddress());
        response.setDeliveryMethod(order.getDeliveryMethod());
        response.setOrderTotal(order.getOrderTotal());
        response.setDeliveryMethod(order.getDeliveryMethod());
        response.setSubtotal(order.getSubtotal());
        response.setDeliveryCost(order.getDeliveryCost());
        response.setOrderTotal(order.getOrderTotal());
        response.setUser(UserResponse.fromEntity(order.getUser()));
        response.setProducts(order.getProducts().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList()));

        return response;
    }
}