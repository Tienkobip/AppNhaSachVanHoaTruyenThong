package com.webbansach.app.models;

public class AddToCartDto {
    public int productId;
    public int quantity;

    public AddToCartDto(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
