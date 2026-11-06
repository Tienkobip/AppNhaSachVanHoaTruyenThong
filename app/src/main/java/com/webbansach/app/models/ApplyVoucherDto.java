package com.webbansach.app.models;

public class ApplyVoucherDto {
    public String code;
    public double currentOrderAmount;

    public ApplyVoucherDto(String code, double currentOrderAmount) {
        this.code = code;
        this.currentOrderAmount = currentOrderAmount;
    }
}
