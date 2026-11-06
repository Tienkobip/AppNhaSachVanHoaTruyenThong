package com.webbansach.app.models;

public class CreateReviewDto {
    public int productId;
    public int orderId;
    public int rating;
    public String comment;

    public CreateReviewDto(int productId, int orderId, int rating, String comment) {
        this.productId = productId;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
    }
}
