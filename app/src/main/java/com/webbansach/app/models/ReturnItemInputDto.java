package com.webbansach.app.models;

public class ReturnItemInputDto {
    public int productId;
    public int quantity;
    public String note;

    public ReturnItemInputDto(int productId, int quantity, String note) {
        this.productId = productId;
        this.quantity = quantity;
        this.note = note;
    }
}
