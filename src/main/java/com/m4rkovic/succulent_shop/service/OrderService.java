package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    public List<Order> findAll();

    public Order findById(Long id);

    public Order save(Long userId, List<Long> productIds);

    public void deleteById(Long orderId);
}
