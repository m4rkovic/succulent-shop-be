package com.m4rkovic.succulent_shop.repository;

import com.m4rkovic.succulent_shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
