package com.webbansach.app.models;

public class OrderListItemDto {
    public int orderId;
    public String orderCode;
    public String orderDate;
    public int status;
    public String statusText;
    public double totalAmount;
    public boolean isPaid;
    public String firstProductTitle;
    public String firstProductImageUrl;
    public int firstProductQuantity;
    public int otherProductsCount;
    public String customerName;
    public String customerPhone;
}
