package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.dto.OrderDTO;
import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import com.m4rkovic.succulent_shop.exceptions.CreationException;
import com.m4rkovic.succulent_shop.exceptions.DeleteException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.mapper.OrderMapper;
import com.m4rkovic.succulent_shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderValidationService validationService;
    private final UserService userService;
    private final ProductService productService;
    private final OrderMapper orderMapper;

    // FIND ALL
    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        log.debug("Retrieving all orders!");
        return orderRepository.findAll();
    }

    // FIND BY ID
    @Override
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        log.debug("Retrieving order with id: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    // SAVE
    @Override
    @Transactional
    public Order save(Long userId, List<Long> productIds) {
        log.debug("Creating a new order for user with id: {}", userId);

        User user = userService.findById(userId);
        List<Product> products = productService.findProductsByIds(productIds);

        OrderDTO orderDTO = createOrderDTO(user, products);
        validationService.validateOrderDTO(orderDTO);

        try {
            Order order = orderMapper.toEntity(orderDTO);
            order = orderRepository.save(order);

            String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String orderCode = "ORD" + order.getId() + datePart;
            order.setOrderCode(orderCode);

            return orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            throw new CreationException("Failed to create order due to data integrity violation", e);
        }
    }

    // UPDATE ORDER STATUS
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.debug("Updating status for order with id: {}", orderId);
        Order order = findById(orderId);

        Date updateDate = new Date(System.currentTimeMillis());
        order.setOrderStatus(newStatus);
        order.setOrderUpdateDate(updateDate);
        order.setOrderUpdateLog(order.getOrderUpdateLog() +
                "Status changed to " + newStatus.name() + " at: " + updateDate + "\n");

        return orderRepository.save(order);
    }

    // DELETE BY ID
    @Override
    @Transactional
    public void deleteById(Long orderId) {
        log.debug("Deleting order with id: {}", orderId);
        findById(orderId);

        try {
            orderRepository.deleteById(orderId);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException("Cannot delete order with id: " + orderId + " due to existing references", e);
        }
    }

    private OrderDTO createOrderDTO(User user, List<Product> products) {
        return OrderDTO.builder()
                .userId(user.getId())
                .productsIds(products.stream().map(Product::getId).toList())
                .orderDate(new Date())
                .orderStatus(OrderStatus.ORDERED)
                .address(user.getAddress())
                .build();
    }
}
