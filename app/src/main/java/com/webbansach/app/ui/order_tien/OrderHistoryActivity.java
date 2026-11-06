package com.webbansach.app.ui.order_tien;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.databinding.ActivityMh12OrderHistoryBinding;
import com.webbansach.app.models.CancelOrderRequestDto;
import com.webbansach.app.models.OrderDto;
import com.webbansach.app.models.OrderListItemDto;
import com.webbansach.app.ui.auth_long.ReturnOrderActivity;
import com.webbansach.app.ui.catalog_hieu.WriteReviewActivity;

import java.util.List;

/**
 * MH12 - Lịch sử & Chi tiết đơn hàng
 * Phụ trách: Nguyễn Việt Tiến
 *
 * Chức năng:
 * - Danh sách đơn hàng (GET /api/orders/my-orders?status=)
 * - Lọc theo tab trạng thái: Tất cả, Chờ xử lý, Đang xử lý, Đang giao, Hoàn thành, Đã hủy, Đã trả
 * - Xem chi tiết đơn (GET /api/orders/{id})
 * - Hủy đơn (PUT /api/orders/{id}/cancel) - chỉ khi Pending
 * - Nút "Trả hàng" -> truyền EXTRA_ORDER_ID sang ReturnOrderActivity (MH13)
 * - Nút "Đánh giá" -> truyền EXTRA_PRODUCT_ID, EXTRA_ORDER_ID sang WriteReviewActivity (MH14)
 */
public class OrderHistoryActivity extends AppCompatActivity {

    private ActivityMh12OrderHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh12OrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO [Tiến]: Thiết kế giao diện lịch sử đơn hàng và xử lý sự kiện
        // Gợi ý:
        // ApiClient.getApi(this).getMyOrders(null).enqueue(new ApiCallback<List<OrderListItemDto>>() { ... });
        // Hủy đơn:
        // ApiClient.getApi(this).cancelOrder(orderId, new CancelOrderRequestDto(reason)).enqueue(...);
        // Nút trả hàng:
        // Intent intent = new Intent(this, ReturnOrderActivity.class);
        // intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
        // startActivity(intent);
    }
}
