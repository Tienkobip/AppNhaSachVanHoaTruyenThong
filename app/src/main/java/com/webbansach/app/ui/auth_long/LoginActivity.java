package com.webbansach.app.ui.auth_long;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.databinding.ActivityMh02LoginBinding;
import com.webbansach.app.models.AuthResponseDto;
import com.webbansach.app.models.CustomerLoginDto;
import com.webbansach.app.ui.catalog_hieu.HomeActivity;

/**
 * MH02 - Đăng nhập
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Nhập Email/SĐT và Mật khẩu
 * - Gọi API POST /api/Auth/mobile-login
 * - Lưu AccessToken + RefreshToken + UserInfo vào TokenManager
 * - Chuyển đến HomeActivity (MH04) khi thành công
 * - Có nút chuyển sang RegisterActivity (MH03)
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityMh02LoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh02LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO [Long]: Thiết kế giao diện đăng nhập và xử lý sự kiện
        // Gợi ý gọi API:
        // CustomerLoginDto loginDto = new CustomerLoginDto(email, password);
        // ApiClient.getApi(this).mobileLogin(loginDto).enqueue(new ApiCallback<AuthResponseDto>() {
        //     @Override
        //     public void onSuccess(AuthResponseDto data, String message) {
        //         TokenManager.getInstance(LoginActivity.this).saveTokens(data.accessToken, data.refreshToken);
        //         TokenManager.getInstance(LoginActivity.this).saveUserInfo(data);
        //         startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        //         finish();
        //     }
        //     @Override
        //     public void onError(String errorMessage) {
        //         Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        //     }
        // });
    }
}
