package com.webbansach.app.models;

public class CreateAddressDto {
    public String receiverName;
    public String receiverPhone;
    public String province = "";
    public String district = "";
    public String ward = "";
    public String detailAddress;
    public boolean setAsDefault;

    public CreateAddressDto(String receiverName, String receiverPhone, String detailAddress, boolean setAsDefault) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.detailAddress = detailAddress;
        this.setAsDefault = setAsDefault;
    }
}
