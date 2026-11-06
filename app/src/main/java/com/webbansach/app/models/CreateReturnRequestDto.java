package com.webbansach.app.models;

import java.util.List;

public class CreateReturnRequestDto {
    public int orderId;
    public String reason;
    public String detailDescription;
    public String bankAccountInfo;
    public String bankName;
    public String bankAccountNumber;
    public String bankAccountHolder;
    public List<ReturnItemInputDto> items;
    public List<String> evidenceImagesBase64;
    public List<String> evidenceImageFileNames;
}
