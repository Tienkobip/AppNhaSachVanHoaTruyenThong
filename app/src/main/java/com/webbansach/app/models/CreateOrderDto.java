package com.webbansach.app.models;

public class CreateOrderDto {
    public String receiverName;
    public String receiverPhone;
    public String province = "";
    public String district = "";
    public String ward = "";
    public String detailAddress;
    public String notes;
    public int paymentMethod; // 0: COD, 1: VNPAY, 2: Momo, 3: BankTransfer
    public String couponCode;
    public Integer savedAddressId;
}
