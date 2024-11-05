package com.m4rkovic.succulent_shop.dto;

import com.m4rkovic.succulent_shop.enumerator.OrderStatus;

import java.util.Date;
import java.util.List;

public class OrderDTO {

    private Long userId;
    private List<Long> productsIds; // List of product IDs
    private String orderCode;
    private Date orderDate;
    private OrderStatus orderStatus;
    private AddressDTO address;

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getProductsIds() {
        return productsIds;
    }

    public void setProductsIds(List<Long> productsIdss) {
        this.productsIds = productsIdss;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}
