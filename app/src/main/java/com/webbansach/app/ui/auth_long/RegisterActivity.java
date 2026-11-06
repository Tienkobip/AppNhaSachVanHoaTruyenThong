package com.webbansach.app.ui.auth_long;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.databinding.ActivityMh03RegisterBinding;
import com.webbansach.app.models.AuthResponseDto;
import com.webbansach.app.models.RegisterDto;
import com.webbansach.app.ui.catalog_hieu.HomeActivity;

/**
 * MH03 - Đăng ký tài khoản
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Nhập Họ tên, Email, SĐT, Mật khẩu, Xác nhận MK, Địa chỉ
 * - Gọi API POST /api/Auth/register
 * - Tự động đăng nhập và lưu token khi đăng ký thành công
 * - Chuyển đến HomeActivity (MH04)
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityMh03RegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh03RegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO [Long]: Thiết kế giao diện đăng ký và xử lý sự kiện
        // Gợi ý:
        // RegisterDto registerDto = new RegisterDto(fullName, email, phone, password, confirmPassword, address);
        // ApiClient.getApi(this).register(registerDto).enqueue(new ApiCallback<AuthResponseDto>() { ... });
    }
}
