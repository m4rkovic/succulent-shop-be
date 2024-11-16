package com.m4rkovic.succulent_shop.controller;

import com.m4rkovic.succulent_shop.dto.OrderDTO;
import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.entity.User;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import com.m4rkovic.succulent_shop.exceptions.InvalidDataException;
import com.m4rkovic.succulent_shop.exceptions.ResourceNotFoundException;
import com.m4rkovic.succulent_shop.response.OrderResponse;
import com.m4rkovic.succulent_shop.service.OrderService;
import com.m4rkovic.succulent_shop.service.ProductService;
import com.m4rkovic.succulent_shop.service.UserService;
import com.m4rkovic.succulent_shop.validator.OrderValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Order Controller", description = "Order management APIs")
@CrossOrigin
@Slf4j
public class OrderApiController {

    private final OrderService orderService;
    private final OrderValidator orderValidator;
    private final ProductService productService;
    private final UserService userService;

    // FIND BY ID
    @Operation(summary = "Get an order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    // FIND ALL
    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.findAll()
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    // FIND BY USER ID
    @Operation(summary = "Get all orders for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found for user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        List<OrderResponse> orders = orderService.findByUserId(userId)
                .stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    // CREATE ORDER
    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderDTO orderDto) {
        log.debug("Creating new order with data: {}", orderDto);

        try {
            orderValidator.validateAndThrow(orderDto);

            // Validate and get user
            User user = userService.findById(orderDto.getUserId());
            if (user == null) {
                throw new ResourceNotFoundException("User not found with id: " + orderDto.getUserId());
            }

            // Validate and get products
            List<Product> products = productService.findProductsByIds(orderDto.getProductsIds());
            if (products.isEmpty()) {
                throw new ResourceNotFoundException("No products found for the provided ids");
            }
            if (products.size() != orderDto.getProductsIds().size()) {
                throw new InvalidDataException("Some products from the provided list were not found");
            }

            // Calculate order total
            BigDecimal orderTotal = products.stream()
                    .map(Product::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Create order with validated data
            Order savedOrder = orderService.save(
                    orderDto.getUserId(),
                    orderDto.getProductsIds(),
                    orderDto.getAddress(),
                    orderDto.getDeliveryMethod(),
                    orderTotal
            );

            OrderResponse response = OrderResponse.fromEntity(savedOrder);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedOrder.getId())
                    .toUri();

            return ResponseEntity.created(location).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid input in order creation request", e);
            throw new InvalidDataException("Invalid order data: " + e.getMessage());
        }
    }

    // UPDATE ORDER STATUS
    @Operation(summary = "Update the status of an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status provided")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID", required = true) @PathVariable Long id,
            @Parameter(description = "New status for the order", required = true) @RequestBody OrderStatus newStatus) {
        log.debug("Updating status for order with id: {} to {}", id, newStatus);

        Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(OrderResponse.fromEntity(updatedOrder));
    }

    // DELETE
    @Operation(summary = "Delete an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        log.debug("Deleting order: {}", id);
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}