package com.webbansach.app.models;

import java.util.List;

public class ReturnRequestSummaryDto {
    public int returnRequestId;
    public int orderId;
    public String orderCode;
    public String reason;
    public String detailDescription;
    public int status;
    public String statusText;
    public String createdAt;
    public String processedAt;
    public String adminNote;
    public double refundAmount;
    public String bankName;
    public String bankAccountNumber;
    public String bankAccountHolder;
    public List<String> evidenceImageUrls;
}
