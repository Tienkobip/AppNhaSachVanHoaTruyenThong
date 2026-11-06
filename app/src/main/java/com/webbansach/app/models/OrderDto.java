package com.webbansach.app.models;

import java.util.List;

public class OrderDto {
    public int orderId;
    public String orderCode;
    public String orderDate;
    public int status;
    public String statusText;
    public String receiverName;
    public String receiverPhone;
    public String shippingAddress;
    public String notes;
    public int paymentMethod;
    public String paymentMethodText;
    public boolean isPaid;
    public double subtotal;
    public double discountAmount;
    public double shippingFee;
    public double totalAmount;
    public String couponCode;
    public String cancelReason;
    public String cancelledAt;
    public List<OrderItemDto> items;
}
