package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.AddressDTO;
import com.m4rkovic.succulent_shop.dto.OrderDTO;
import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.repository.OrderRepository;
import com.m4rkovic.succulent_shop.service.OrderService;
import com.m4rkovic.succulent_shop.service.ProductService;
import com.m4rkovic.succulent_shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin
public class OrderApiController {
//
//    private final OrderRepository orderRepository;
//    private final OrderService orderService;
//
//    private final UserService userService;
//    private final ProductService productService;
//
//    @GetMapping("/{id}")
//    public Order getOrders(@PathVariable Long id) {
//        return orderService.findById(id);
//    }
//
//    @GetMapping("/allOrders")
//    public List<Order> getOrders() {
//        return orderService.findAll();
//    }
//
//    @PostMapping("/createOrder")
//    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) throws URISyntaxException {
//        System.out.println("Received OrderDto: " + orderDTO);
//        System.out.println("Received user id: " + orderDTO.getUserId());
//        System.out.println("Received product ids: " + orderDTO.getProductsIds());
//
//        if (orderDTO.getUserId() == null) {
//            return ResponseEntity.badRequest().body(null);
//        }
//
//        if (orderDTO.getProductsIds() == null || orderDTO.getProductsIds().isEmpty()) {
//            return ResponseEntity.badRequest().body(null); // Ensure the product list is not empty
//        }
//
//        User user = userService.findById(orderDTO.getUserId());
//        if (user == null) {
//            return ResponseEntity.badRequest().body(null); // Return bad request if user is not found
//        }
//
//        // Retrieve all products based on the provided product IDs
//        List<Product> products = new ArrayList<>();
//        for (Long productId : orderDTO.getProductsIds()) {
//            Product product = productService.findById(productId);
//            if (product == null) {
//                return ResponseEntity.badRequest().body(null); // Return bad request if any product is not found
//            }
//            products.add(product);
//        }
//
//        // Map AddressDTO to Address entity
////        Address address = new Address();
////        AddressDTO addressDTO = orderDTO.getAddress();
////        if (addressDTO != null) {
////            address.setStreet(addressDTO.getStreet());
////            address.setCity(addressDTO.getCity());
////            address.setPostalCode(addressDTO.getPostalCode());
////            address.setCountry(addressDTO.getCountry());
////        }
//
//        Order savedOrder = orderService.save(user, products); // Pass the address to the service
//
//        // Create and populate OrderDTO to return
//        OrderDTO responseDto = new OrderDTO();
//        responseDto.setUserId(user.getId());
//        responseDto.setProductsIds(orderDTO.getProductsIds());
//        responseDto.setOrderCode(savedOrder.getOrderCode());
//        responseDto.setOrderDate(savedOrder.getOrderDate());
//        responseDto.setOrderStatus(savedOrder.getOrderStatus());
//
//        // Set the address details in response DTO if needed
////        if (savedOrder.getAddress() != null) {
////            Address savedAddress = savedOrder.getAddress();
////            AddressDTO responseAddressDTO = new AddressDTO();
////            responseAddressDTO.setStreet(savedAddress.getStreet());
////            responseAddressDTO.setCity(savedAddress.getCity());
////            responseAddressDTO.setPostalCode(savedAddress.getPostalCode());
////            responseAddressDTO.setCountry(savedAddress.getCountry());
////            responseDto.setAddress(responseAddressDTO);
////        }
//
//        return ResponseEntity.created(new URI("/orders/" + savedOrder.getId())).body(responseDto);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity updateOrder(@PathVariable Long id, @RequestBody Order order) {
//        Order currentOrder = orderService.findById(id);
//        currentOrder.setOrderStatus(order.getOrderStatus());
//        currentOrder.setOrderUpdateDate(new Date(System.currentTimeMillis()));
//        currentOrder.setOrderUpdateLog(order.getOrderUpdateLog() + "Order updated! At: " + order.getOrderUpdateDate() + "Status: " + order.getOrderStatus() + "\n");
//        //currentOrder.setAddress(order.getAddress());
//        currentOrder.setProducts(order.getProducts());
//        currentOrder = orderRepository.save(order);
//
//        return ResponseEntity.ok(currentOrder);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity deleteOrder(@PathVariable Long id) {
//        orderService.deleteById(id);
//        return ResponseEntity.ok().build();
//    }
}
