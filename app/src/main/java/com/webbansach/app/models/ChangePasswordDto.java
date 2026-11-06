package com.webbansach.app.models;

public class ChangePasswordDto {
    public String currentPassword;
    public String newPassword;
    public String confirmNewPassword;

    public ChangePasswordDto(String currentPassword, String newPassword, String confirmNewPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }
}
