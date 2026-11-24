package com.webbansach.app.models;

public class UpdateProfileDto {
    public String fullName;
    public String phoneNumber;
    public String dateOfBirth; // Định dạng "yyyy-MM-dd" hoặc null (KHÔNG truyền chuỗi rỗng "")
    public String address;
    public String avatarUrl;
    public String avatarBase64;
    public String avatarFileName;

    public UpdateProfileDto() {
    }

    public UpdateProfileDto(String fullName, String phoneNumber, String dateOfBirth, String address, String avatarBase64, String avatarFileName) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) ? dateOfBirth.trim() : null;
        this.address = address;
        this.avatarBase64 = avatarBase64;
        this.avatarFileName = avatarFileName;
    }
}
