package com.webbansach.app.ui.auth_long;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.databinding.ActivityMh10ProfileBinding;
import com.webbansach.app.models.AuthResponseDto;
import com.webbansach.app.models.ChangePasswordDto;
import com.webbansach.app.models.RevokeTokenRequestDto;
import com.webbansach.app.models.UpdateProfileDto;
import com.webbansach.app.ui.order_tien.AddressListActivity;
import com.webbansach.app.ui.order_tien.OrderHistoryActivity;

/**
 * MH10 - Tài khoản / Hồ sơ cá nhân
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Hiển thị thông tin cá nhân (GET /api/Auth/me)
 * - Cập nhật hồ sơ (PUT /api/Auth/profile)
 * - Đổi mật khẩu (PUT /api/Auth/change-password)
 * - Đăng xuất (POST /api/Auth/revoke-token -> clearTokens -> LoginActivity)
 * - Nút chuyển đến Sổ địa chỉ (MH11) và Lịch sử đơn hàng (MH12)
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityMh10ProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh10ProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO [Long]: Thiết kế giao diện hồ sơ và xử lý sự kiện
        // Gợi ý:
        // - Gọi ApiClient.getApi(this).getMe().enqueue(...) để tải thông tin
        // - Nút "Sổ địa chỉ": startActivity(new Intent(this, AddressListActivity.class))
        // - Nút "Lịch sử đơn": startActivity(new Intent(this, OrderHistoryActivity.class))
        // - Nút "Đăng xuất": gọi revokeToken rồi TokenManager.clearTokens() rồi chuyển LoginActivity
    }
}
