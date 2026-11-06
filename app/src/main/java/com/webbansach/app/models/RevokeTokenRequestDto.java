package com.webbansach.app.models;

public class RevokeTokenRequestDto {
    public String refreshToken;

    public RevokeTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
