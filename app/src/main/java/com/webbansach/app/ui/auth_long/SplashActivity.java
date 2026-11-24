package com.webbansach.app.ui.auth_long;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.R;
import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.databinding.ActivityMh01SplashBinding;
import com.webbansach.app.models.AuthResponseDto;
import com.webbansach.app.ui.catalog_hieu.HomeActivity;

/**
 * MH01 - Splash Screen (Màn hình khởi động)
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Hiển thị logo thương hiệu trong 1.2 giây
 * - Kiểm tra phiên đăng nhập an toàn với TokenManager và Server API (GET /api/Auth/me)
 * - Nếu chưa đăng nhập / phiên hết hạn -> Chuyển đến LoginActivity (MH02)
 * - Nếu phiên đăng nhập hợp lệ -> Cập nhật thông tin người dùng và chuyển đến HomeActivity (MH04)
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 1200L;

    private ActivityMh01SplashBinding binding;
    private final Handler splashHandler = new Handler(Looper.getMainLooper());
    private boolean isNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh01SplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bắt đầu chuỗi kiểm tra phiên sau khoảng trễ 1200ms
        splashHandler.postDelayed(this::checkAuthSession, SPLASH_DELAY_MS);
    }

    private void checkAuthSession() {
        if (isFinishing() || isDestroyed()) return;

        TokenManager tokenManager = TokenManager.getInstance(this);

        // Trường hợp 1: Chưa đăng nhập hoặc token không hợp lệ định dạng
        if (!tokenManager.isLoggedIn()) {
            tokenManager.clearTokens();
            navigateToLogin();
            return;
        }

        // Trường hợp 2: Có token trong máy, gọi API getMe() để xác thực phiên với Server
        if (binding.tvSplashStatus != null) {
            binding.tvSplashStatus.setText(R.string.long_splash_status_checking);
            binding.tvSplashStatus.setVisibility(View.VISIBLE);
        }

        ApiClient.getApi(this).getMe().enqueue(new ApiCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto data, String message) {
                if (isFinishing() || isDestroyed()) return;

                if (data != null) {
                    // Cập nhật thông tin hồ sơ mới nhất vào SharedPreferences
                    tokenManager.saveUserInfo(data);
                }
                navigateToHome();
            }

            @Override
            public void onError(String errorMessage) {
                if (isFinishing() || isDestroyed()) return;

                // Nếu TokenAuthenticator phát hiện phiên chết (401 không refresh được) thì đã clearTokens()
                if (!tokenManager.isLoggedIn()) {
                    navigateToLogin();
                } else {
                    // Nếu lỗi do timeout mạng tạm thời nhưng local token vẫn tồn tại -> vẫn vào Home
                    navigateToHome();
                }
            }
        });
    }

    private synchronized void navigateToLogin() {
        if (isNavigated) return;
        isNavigated = true;

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private synchronized void navigateToHome() {
        if (isNavigated) return;
        isNavigated = true;

        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy bỏ callback chờ để tránh memory leak và không kích hoạt điều hướng sau khi Activity đã bị hủy
        splashHandler.removeCallbacksAndMessages(null);
    }
}
