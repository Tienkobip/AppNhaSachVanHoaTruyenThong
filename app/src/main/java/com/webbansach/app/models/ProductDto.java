package com.webbansach.app.models;

import java.util.List;

public class ProductDto {
    public int productId;
    public String title;
    public String authorName;
    public int authorId;
    public String categoryName;
    public int categoryId;
    public String description;
    public double price;
    public double discountPercent;
    public double finalPrice;
    public String mainImageUrl;
    public List<String> imageUrls;
    public int stockQuantity;
    public boolean isActive;
    public double averageRating;
    public int reviewCount;
    public String publisher;
    public String publishYear;
    public int pageCount;
    public String isbn;
    public String language;
    public String dimensions;
    public double weight;
}
