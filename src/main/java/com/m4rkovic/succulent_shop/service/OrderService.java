package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface OrderService {
    public List<Order> findAll();

    public Order findById(Long id);

    public Order save(Long userId, List<Long> productIds, String address, String deliveryMethod, BigDecimal orderTotal);

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus);

    public void deleteById(Long orderId);

    List<Order> findByUserId(Long userId);
}
