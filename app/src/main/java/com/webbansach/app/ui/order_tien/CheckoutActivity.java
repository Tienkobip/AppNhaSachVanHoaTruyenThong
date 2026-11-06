package com.webbansach.app.ui.order_tien;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.core.utils.CurrencyUtils;
import com.webbansach.app.core.utils.GpsLocationHelper;
import com.webbansach.app.databinding.ActivityMh08CheckoutBinding;
import com.webbansach.app.models.ApplyVoucherDto;
import com.webbansach.app.models.CartDto;
import com.webbansach.app.models.CreateOrderDto;
import com.webbansach.app.models.OrderDto;
import com.webbansach.app.models.VoucherApplyResultDto;

/**
 * MH08 - Thanh toán (Checkout) - Tích hợp GPS
 * Phụ trách: Nguyễn Việt Tiến
 *
 * Chức năng:
 * - Tải lại giỏ hàng (GET /api/cart) để hiển thị tóm tắt
 * - Chọn/Nhập địa chỉ giao hàng (GPS hoặc chọn từ Sổ địa chỉ MH11)
 * - Nhập mã giảm giá (POST /api/orders/apply-voucher)
 * - Chọn phương thức thanh toán (COD, VNPay, Momo, Bank Transfer)
 * - Đặt hàng (POST /api/orders/checkout)
 * - Thành công -> truyền EXTRA_ORDER_ID, EXTRA_ORDER_CODE sang MH09
 */
public class CheckoutActivity extends AppCompatActivity {

    private ActivityMh08CheckoutBinding binding;
    private GpsLocationHelper gpsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh08CheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gpsHelper = new GpsLocationHelper(this);

        // TODO [Tiến]: Thiết kế giao diện thanh toán và xử lý sự kiện
        // Gợi ý GPS:
        // gpsHelper.getCurrentAddress(new GpsLocationHelper.OnAddressListener() {
        //     @Override public void onAddressFound(String fullAddress) { /* điền vào ô địa chỉ */ }
        //     @Override public void onError(String errorMessage) { Toast.makeText(...); }
        // });
        //
        // Gợi ý Voucher:
        // ApiClient.getApi(this).applyVoucher(new ApplyVoucherDto(code, amount)).enqueue(...);
        //
        // Gợi ý Checkout:
        // CreateOrderDto orderDto = new CreateOrderDto();
        // orderDto.receiverName = ...; orderDto.paymentMethod = 0; ...
        // ApiClient.getApi(this).checkout(orderDto).enqueue(new ApiCallback<OrderDto>() {
        //     @Override public void onSuccess(OrderDto data, String message) {
        //         Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
        //         intent.putExtra(Constants.EXTRA_ORDER_ID, data.orderId);
        //         intent.putExtra(Constants.EXTRA_ORDER_CODE, data.orderCode);
        //         startActivity(intent);
        //         finish();
        //     }
        // });
    }
}
