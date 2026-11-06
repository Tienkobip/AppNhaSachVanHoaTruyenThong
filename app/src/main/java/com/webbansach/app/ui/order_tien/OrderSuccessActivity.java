package com.webbansach.app.ui.order_tien;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.databinding.ActivityMh09SuccessBinding;
import com.webbansach.app.ui.catalog_hieu.HomeActivity;

/**
 * MH09 - Đặt hàng thành công
 * Phụ trách: Nguyễn Việt Tiến
 *
 * Chức năng:
 * - Nhận EXTRA_ORDER_ID và EXTRA_ORDER_CODE từ CheckoutActivity
 * - Hiển thị thông tin đặt hàng thành công (mã đơn, thông báo)
 * - Nút "Xem đơn hàng" -> OrderHistoryActivity (MH12)
 * - Nút "Tiếp tục mua sắm" -> HomeActivity (MH04)
 */
public class OrderSuccessActivity extends AppCompatActivity {

    private ActivityMh09SuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh09SuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);
        String orderCode = getIntent().getStringExtra(Constants.EXTRA_ORDER_CODE);

        // TODO [Tiến]: Thiết kế giao diện thành công và xử lý sự kiện
        // - Hiển thị orderCode
        // - Nút "Xem đơn hàng" -> startActivity(new Intent(this, OrderHistoryActivity.class))
        // - Nút "Về trang chủ" -> startActivity(new Intent(this, HomeActivity.class))
    }
}
