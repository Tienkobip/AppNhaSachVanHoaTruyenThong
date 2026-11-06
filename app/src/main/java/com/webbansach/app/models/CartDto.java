package com.webbansach.app.models;

import java.util.List;

public class CartDto {
    public int cartId;
    public List<CartItemDto> items;
    public double totalAmount;
    public int totalItems;
}
