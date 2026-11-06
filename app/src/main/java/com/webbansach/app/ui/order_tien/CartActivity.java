package com.webbansach.app.ui.order_tien;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.CurrencyUtils;
import com.webbansach.app.databinding.ActivityMh07CartBinding;
import com.webbansach.app.models.CartDto;
import com.webbansach.app.models.UpdateCartItemQuantityDto;

/**
 * MH07 - Giỏ hàng
 * Phụ trách: Nguyễn Việt Tiến
 *
 * Chức năng:
 * - Tải giỏ hàng (GET /api/cart)
 * - Hiển thị danh sách sản phẩm trong giỏ (RecyclerView)
 * - Tăng/Giảm số lượng (PUT /api/cart/items/{productId})
 * - Xóa sản phẩm (DELETE /api/cart/items/{productId})
 * - Hiển thị tổng tiền
 * - Nút "Thanh toán" -> CheckoutActivity (MH08)
 */
public class CartActivity extends AppCompatActivity {

    private ActivityMh07CartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh07CartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO [Tiến]: Thiết kế giao diện giỏ hàng và xử lý sự kiện
        // Gợi ý:
        // ApiClient.getApi(this).getCart().enqueue(new ApiCallback<CartDto>() {
        //     @Override
        //     public void onSuccess(CartDto data, String message) {
        //         // Hiển thị data.items vào RecyclerView
        //         // binding.tvTotalAmount.setText(CurrencyUtils.formatVnd(data.totalAmount));
        //     }
        //     @Override
        //     public void onError(String errorMessage) {
        //         Toast.makeText(CartActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        //     }
        // });
    }
}
