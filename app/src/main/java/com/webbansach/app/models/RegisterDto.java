package com.webbansach.app.models;

public class RegisterDto {
    public String fullName;
    public String email;
    public String phoneNumber;
    public String password;
    public String confirmPassword;
    public String address;

    public RegisterDto(String fullName, String email, String phoneNumber, String password, String confirmPassword, String address) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.address = address;
    }
}
