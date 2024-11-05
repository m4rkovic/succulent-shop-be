package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.*;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import com.m4rkovic.succulent_shop.enumerator.PotSize;
import com.m4rkovic.succulent_shop.enumerator.ProductType;
import com.m4rkovic.succulent_shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Long id) {
        Optional<Order> result = orderRepository.findById(id);

        Order order = null;
        if (result.isPresent()) {
            order = result.get();
        } else {
            throw new RuntimeException("Order with id " + id + "has not been found!");
        }
        return order;
    }

    @Override
    public Order save(User user, List<Product> products) {
        Random rand = new Random();
        int randInt = rand.nextInt(1000);

        Order order = new Order();
        order.setOrderUpdateLog("Order created at: " + order.getOrderDate() + "\n");
        order.setOrderStatus(OrderStatus.ORDERED);
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setOrderUpdateDate(null);
        order.setUser(user);
        order.setOrderCode("ORD" + randInt + new Date(System.currentTimeMillis()).toString());
        order.setProducts(products); // Set the list of products
        order.setAddress(user.getAddress()); // Set the address

        orderRepository.save(order);
        return order;
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = findById(orderId);

        Date updateDate = new Date(System.currentTimeMillis());
        order.setOrderStatus(newStatus);
        order.setOrderUpdateDate(updateDate);
        order.setOrderUpdateLog(order.getOrderUpdateLog() +
                "Status changed to " + newStatus.name() + " at: " + updateDate + "\n");

        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}
