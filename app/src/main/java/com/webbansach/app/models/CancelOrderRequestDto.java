package com.webbansach.app.models;

public class CancelOrderRequestDto {
    public String cancelReason;

    public CancelOrderRequestDto(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
