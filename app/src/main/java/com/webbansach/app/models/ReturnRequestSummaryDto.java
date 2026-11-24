package com.webbansach.app.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReturnRequestSummaryDto {
    public int returnRequestId;
    public int orderId;
    public String orderCode;
    public int status;
    public String statusText;
    public String reason;
    public String bankAccountInfo;
    public double refundAmount;
    public String requestedAt;
    public String proofImageUrl;
    public String processedAt;

    /**
     * Tách chuỗi proofImageUrl (ngăn cách bởi dấu ';') thành danh sách URL ảnh.
     */
    public List<String> getProofImageUrlList() {
        if (proofImageUrl == null || proofImageUrl.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(proofImageUrl.split(";"));
    }
}
