package com.webbansach.app.models;

import java.util.List;

public class AuthResponseDto {
    public String userId;
    public String fullName;
    public String email;
    public String avatarUrl;
    public List<String> roles;
    public String securityStamp;
    public String accessToken;
    public String refreshToken;
    public String accessTokenExpiration;
    public String phoneNumber;
    public String dateOfBirth;
    public String address;
    public String gender;
}
