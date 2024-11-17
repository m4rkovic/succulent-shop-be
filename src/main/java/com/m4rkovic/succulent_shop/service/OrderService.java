package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.DeliveryMethod;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface OrderService {
    public List<Order> findAll();

    public Order findById(Long id);

    public Page<Order> findAllPaginated(Pageable pageable);

    public Page<Order> findByUserIdPaginated(Long userId, Pageable pageable);

    public Order save(Long userId, List<Long> productIds, String address, DeliveryMethod deliveryMethod);

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus);

    public void deleteById(Long orderId);

    List<Order> findByUserId(Long userId);
}
