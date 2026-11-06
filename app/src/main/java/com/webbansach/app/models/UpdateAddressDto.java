package com.webbansach.app.models;

public class UpdateAddressDto {
    public int addressId;
    public String receiverName;
    public String receiverPhone;
    public String province = "";
    public String district = "";
    public String ward = "";
    public String detailAddress;
    public boolean setAsDefault;

    public UpdateAddressDto(int addressId, String receiverName, String receiverPhone, String detailAddress, boolean setAsDefault) {
        this.addressId = addressId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.detailAddress = detailAddress;
        this.setAsDefault = setAsDefault;
    }
}
