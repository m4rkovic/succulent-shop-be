package com.m4rkovic.succulent_shop.utils;

import com.m4rkovic.succulent_shop.entity.Order;
import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.enumerator.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

public class EmailTemplates {

    public static String getStatusSpecificContent(OrderStatus status) {
        return switch (status) {
            case ORDERED -> """
                <p>We've received your order and will begin processing it shortly.</p>
                <p>You'll receive another email once we start preparing your plants for shipment.</p>
                """;
            case PROCESSING -> """
                <p>Great news! We're now preparing your plants for shipment.</p>
                <p>We carefully package each succulent to ensure they arrive in perfect condition.</p>
                <p>You'll receive a shipping notification once your order is on its way.</p>
                """;
            case SHIPPED -> """
                <p>Your plants are on their way! 🌵</p>
                <p>Your order has been carefully packaged and handed over to our delivery partner.</p>
                <p>Estimated delivery: 2-3 business days</p>
                <p>Tips while you wait:</p>
                <ul>
                    <li>Prepare a sunny spot for your new plants</li>
                    <li>Have well-draining soil ready</li>
                    <li>Clean and prepare your pots</li>
                </ul>
                """;
            case DELIVERED -> """
                <p>Your plants have been delivered! 🎉</p>
                <p>Care tips for your new succulents:</p>
                <ul>
                    <li>Let them adjust to their new home for 1-2 days</li>
                    <li>Place in bright, indirect sunlight</li>
                    <li>Water sparingly - less is more!</li>
                </ul>
                <p>We hope you enjoy your new plants!</p>
                """;
            case CANCELLED -> """
                <p>We're sorry to inform you that your order has been cancelled.</p>
                <p>If you didn't request this cancellation, please contact our customer service.</p>
                <p>If you did cancel the order, any payment will be refunded within 3-5 business days.</p>
                """;
        };
    }

    public static String buildEmailTemplate(Order order, String specificContent) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .order-info { background-color: #f9f9f9; padding: 15px; margin: 20px 0; border-radius: 5px; }
                    .products { margin: 20px 0; }
                    .product-item { padding: 10px; border-bottom: 1px solid #eee; }
                    .total { font-weight: bold; padding: 15px; background-color: #f5f5f5; }
                    .footer { text-align: center; margin-top: 20px; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Succulent Shop</h2>
                    </div>
                    
                    <p>Dear %s,</p>
                    
                    <div class="order-info">
                        <h3>Order Details</h3>
                        <p>Order Number: %s</p>
                        <p>Status: %s</p>
                        <p>Delivery Address: %s</p>
                        <p>Delivery Method: %s</p>
                    </div>
                    
                    %s
                    
                    <div class="products">
                        <h3>Products</h3>
                        %s
                    </div>
                    
                    <div class="total">
                        Total Amount: $%.2f
                    </div>
                    
                    <div class="footer">
                        <p>Thank you for choosing Succulent Shop!</p>
                        <p>If you have any questions, please contact our support team.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                order.getUser().getFirstname(),
                order.getOrderCode(),
                order.getOrderStatus(),
                order.getAddress(),
                order.getDeliveryMethod(),
                specificContent,
                formatOrderProductsHtml(order.getProducts()),
                order.getSubtotal()
        );
    }

    private static String formatOrderProductsHtml(List<Product> products) {
        return products.stream()
                .map(product -> String.format("""
                <div class="product-item">
                    <strong>%s</strong><br>
                    Price: $%.2f
                </div>
                """,
                        product.getProductName(),
                        product.getPrice()))
                .collect(Collectors.joining("\n"));
    }
}