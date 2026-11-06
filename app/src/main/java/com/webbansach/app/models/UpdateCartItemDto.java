package com.webbansach.app.models;

public class UpdateCartItemDto {
    public int productId;
    public int quantity;

    public UpdateCartItemDto(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
