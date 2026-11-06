package com.webbansach.app.models;

public class CustomerLoginDto {
    public String emailOrPhone;
    public String password;

    public CustomerLoginDto(String emailOrPhone, String password) {
        this.emailOrPhone = emailOrPhone;
        this.password = password;
    }
}
