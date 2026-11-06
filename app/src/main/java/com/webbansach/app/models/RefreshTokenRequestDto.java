package com.webbansach.app.models;

public class RefreshTokenRequestDto {
    public String accessToken;
    public String refreshToken;

    public RefreshTokenRequestDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
