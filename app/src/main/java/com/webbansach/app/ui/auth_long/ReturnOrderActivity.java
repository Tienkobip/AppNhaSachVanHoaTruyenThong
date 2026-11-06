package com.webbansach.app.ui.auth_long;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.databinding.ActivityMh13ReturnBinding;
import com.webbansach.app.models.CreateReturnRequestDto;
import com.webbansach.app.models.OrderDto;
import com.webbansach.app.models.ReturnRequestSummaryDto;

import java.util.List;

/**
 * MH13 - Yêu cầu trả hàng
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Nhận EXTRA_ORDER_ID từ OrderHistoryActivity (MH12) qua Intent
 * - Tải chi tiết đơn hàng (GET /api/orders/{id}) để hiển thị danh sách sản phẩm
 * - Nhập lý do trả, mô tả chi tiết, thông tin ngân hàng, ảnh minh chứng
 * - Gửi yêu cầu trả hàng (POST /api/ReturnRequests)
 * - Xem danh sách yêu cầu trả hàng đã gửi (GET /api/ReturnRequests/my-returns)
 */
public class ReturnOrderActivity extends AppCompatActivity {

    private ActivityMh13ReturnBinding binding;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh13ReturnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận orderId từ Intent
        orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);

        // TODO [Long]: Thiết kế giao diện trả hàng và xử lý sự kiện
        // Gợi ý:
        // - Gọi ApiClient.getApi(this).getOrderDetail(orderId) để lấy thông tin đơn
        // - Cho user chọn sản phẩm muốn trả, nhập lý do, thông tin bank
        // - Gọi ApiClient.getApi(this).createReturnRequest(dto) để gửi yêu cầu
    }
}
