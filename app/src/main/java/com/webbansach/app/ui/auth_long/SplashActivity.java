package com.webbansach.app.ui.auth_long;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.databinding.ActivityMh01SplashBinding;
import com.webbansach.app.ui.catalog_hieu.HomeActivity;

/**
 * MH01 - Splash Screen (Màn hình khởi động)
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Hiển thị logo và tên ứng dụng trong 2 giây
 * - Kiểm tra trạng thái đăng nhập (TokenManager.isLoggedIn())
 * - Nếu đã đăng nhập -> chuyển thẳng đến HomeActivity (MH04)
 * - Nếu chưa đăng nhập -> chuyển đến LoginActivity (MH02)
 */
public class SplashActivity extends AppCompatActivity {

    private ActivityMh01SplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh01SplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Delay 2 giây rồi kiểm tra đăng nhập
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TokenManager tokenManager = TokenManager.getInstance(this);
            Intent intent;
            if (tokenManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
