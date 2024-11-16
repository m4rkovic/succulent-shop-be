package com.m4rkovic.succulent_shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@SuperBuilder
@Table(name = "_order")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order extends AbstractEntity {
    private Date orderDate;
    private Date orderUpdateDate;

    @Column(length = 2000)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;  // Default value

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String orderCode;
    private String orderUpdateLog;
    private String address;
    private String deliveryMethod;

    @Column(precision = 10, scale = 2)
    private BigDecimal orderTotal;

    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        orderDate = new Date();
        orderUpdateDate = new Date();
        if (orderStatus == null) {
            orderStatus = OrderStatus.ORDERED;
        }
        if (orderUpdateLog == null) {
            orderUpdateLog = "Order created at: " + orderDate + "\n";
        }
        if (deliveryMethod == null) {
            deliveryMethod = "Standard Delivery";
        }
    }
}