package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.DeliveryMethod;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import com.m4rkovic.succulent_shop.exceptions.CreationException;
import com.m4rkovic.succulent_shop.exceptions.DeleteException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.repository.OrderRepository;
import com.m4rkovic.succulent_shop.utils.EmailTemplates;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;
    private final JavaMailSender emailSender;

    // FIND BY USER ID
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        log.debug("Retrieving all orders for user with id: {}", userId);
        userService.findById(userId);
        return orderRepository.findByUserId(userId);
    }

    // FIND ALL
    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        log.debug("Retrieving all orders!");
        return orderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAllPaginated(Pageable pageable) {
        log.debug("Retrieving all orders with pagination! Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return orderRepository.findAll(pageable);
    }

    // FIND BY ID
    @Override
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        log.debug("Retrieving order with id: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findByUserIdPaginated(Long userId, Pageable pageable) {
        log.debug("Retrieving all orders for user with id: {} with pagination! Page: {}, Size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        userService.findById(userId);
        return orderRepository.findByUserId(userId, pageable);
    }

    // SAVE
    @Override
    @Transactional
    public Order save(Long userId, List<Long> productIds, String address, DeliveryMethod deliveryMethod) {
        log.debug("Creating a new order for user with id: {}", userId);

        try {
            User user = userService.findById(userId);
            List<Product> products = productService.findProductsByIds(productIds);

            Order order = Order.builder()
                    .user(user)
                    .products(products)
                    .address(address)
                    .deliveryMethod(deliveryMethod)
                    .orderStatus(OrderStatus.ORDERED)
                    .build();

            order.calculateTotals();
            order = orderRepository.save(order);

            String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String orderCode = "ORD" + order.getId() + datePart;
            order.setOrderCode(orderCode);

            order = orderRepository.save(order);

            // Send order confirmation email asynchronously
            String specificContent = EmailTemplates.getStatusSpecificContent(OrderStatus.ORDERED);
            String htmlContent = EmailTemplates.buildEmailTemplate(order, specificContent);
            sendOrderEmail(order, "Order Confirmation - " + order.getOrderCode(), htmlContent);

            return order;

        } catch (DataIntegrityViolationException e) {
            throw new CreationException("Failed to create order due to data integrity violation", e);
        }
    }

    // UPDATE ORDER STATUS
    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.debug("Updating status for order with id: {}", orderId);
        Order order = findById(orderId);

        Date updateDate = new Date(System.currentTimeMillis());
        order.setOrderStatus(newStatus);
        order.setOrderUpdateDate(updateDate);
        order.setOrderUpdateLog(order.getOrderUpdateLog() +
                "Status changed to " + newStatus.name() + " at: " + updateDate + "\n");

        order = orderRepository.save(order);

        // Send status update email asynchronously
        String specificContent = EmailTemplates.getStatusSpecificContent(newStatus);
        String htmlContent = EmailTemplates.buildEmailTemplate(order, specificContent);
        sendOrderEmail(order, "Order Status Update - " + order.getOrderCode(), htmlContent);

        return order;
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

    // Async email sending
    @Async
    protected void sendOrderEmail(Order order, String subject, String htmlContent) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getUser().getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            emailSender.send(message);
            log.debug("Email sent for order: {}", order.getOrderCode());
        } catch (MessagingException e) {
            log.error("Failed to send email for order: " + order.getOrderCode(), e);
        }
    }
}

//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class OrderServiceImpl implements OrderService {
//
//    private final OrderRepository orderRepository;
//    private final UserService userService;
//    private final ProductService productService;
//
//
//    // FIND BY USER ID
//    @Override
//    @Transactional(readOnly = true)
//    public List<Order> findByUserId(Long userId) {
//        log.debug("Retrieving all orders for user with id: {}", userId);
//        userService.findById(userId);
//        return orderRepository.findByUserId(userId);
//    }
//
//    // FIND ALL
//    @Override
//    @Transactional(readOnly = true)
//    public List<Order> findAll() {
//        log.debug("Retrieving all orders!");
//        return orderRepository.findAll();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Page<Order> findAllPaginated(Pageable pageable) {
//        log.debug("Retrieving all orders with pagination! Page: {}, Size: {}",
//                pageable.getPageNumber(), pageable.getPageSize());
//        return orderRepository.findAll(pageable);
//    }
//
//    // FIND BY ID
//    @Override
//    @Transactional(readOnly = true)
//    public Order findById(Long id) {
//        log.debug("Retrieving order with id: {}", id);
//        return orderRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public Page<Order> findByUserIdPaginated(Long userId, Pageable pageable) {
//        log.debug("Retrieving all orders for user with id: {} with pagination! Page: {}, Size: {}",
//                userId, pageable.getPageNumber(), pageable.getPageSize());
//        userService.findById(userId);
//        return orderRepository.findByUserId(userId, pageable);
//    }
//
//    // SAVE
//    @Override
//    @Transactional
//    public Order save(Long userId, List<Long> productIds, String address, DeliveryMethod deliveryMethod) {
//        log.debug("Creating a new order for user with id: {}", userId);
//
//        try {
//            User user = userService.findById(userId);
//            List<Product> products = productService.findProductsByIds(productIds);
//
//            Order order = Order.builder()
//                    .user(user)
//                    .products(products)
//                    .address(address)
//                    .deliveryMethod(deliveryMethod)
//                    .build();
//
//            order.calculateTotals();
//            order = orderRepository.save(order);
//
//            String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
//            String orderCode = "ORD" + order.getId() + datePart;
//            order.setOrderCode(orderCode);
//
//            return orderRepository.save(order);
//
//        } catch (DataIntegrityViolationException e) {
//            throw new CreationException("Failed to create order due to data integrity violation", e);
//        }
//    }
//
//
//    // UPDATE ORDER STATUS
//    @Override
//    @Transactional
//    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
//        log.debug("Updating status for order with id: {}", orderId);
//        Order order = findById(orderId);
//
//        Date updateDate = new Date(System.currentTimeMillis());
//        order.setOrderStatus(newStatus);
//        order.setOrderUpdateDate(updateDate);
//        order.setOrderUpdateLog(order.getOrderUpdateLog() +
//                "Status changed to " + newStatus.name() + " at: " + updateDate + "\n");
//
//        return orderRepository.save(order);
//    }
//
//    // DELETE BY ID
//    @Override
//    @Transactional
//    public void deleteById(Long orderId) {
//        log.debug("Deleting order with id: {}", orderId);
//        findById(orderId);
//
//        try {
//            orderRepository.deleteById(orderId);
//        } catch (DataIntegrityViolationException e) {
//            throw new DeleteException("Cannot delete order with id: " + orderId + " due to existing references", e);
//        }
//    }
//}
