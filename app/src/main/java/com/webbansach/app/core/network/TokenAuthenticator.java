package com.webbansach.app.core.network;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.models.ApiResponse;
import com.webbansach.app.models.AuthResponseDto;
import com.webbansach.app.models.RefreshTokenRequestDto;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * OkHttp Authenticator tự động refresh token khi nhận được HTTP 401 Unauthorized.
 * Nếu refresh thành công: lưu cặp token mới và gửi lại request.
 * Nếu refresh thất bại: xóa token, đẩy người dùng về LoginActivity.
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public class TokenAuthenticator implements Authenticator {

    private final TokenManager tokenManager;
    private final Context appContext;

    public TokenAuthenticator(Context context) {
        this.appContext = context.getApplicationContext();
        this.tokenManager = TokenManager.getInstance(appContext);
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        // Tránh lặp vô hạn: nếu request trước đó đã thử refresh thì dừng
        if (responseCount(response) >= 2) {
            navigateToLogin();
            return null;
        }

        String currentAccessToken = tokenManager.getAccessToken();
        String currentRefreshToken = tokenManager.getRefreshToken();

        // Nếu không có refresh token thì không thể refresh -> đăng xuất
        if (currentRefreshToken == null || currentRefreshToken.isEmpty()) {
            navigateToLogin();
            return null;
        }

        // Gọi đồng bộ API refresh-token (dùng Retrofit riêng, không có Authenticator để tránh loop)
        try {
            OkHttpClient plainClient = ApiClient.getUnsafeOkHttpClientBuilder().build();

            Retrofit plainRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(plainClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AppApiService plainApi = plainRetrofit.create(AppApiService.class);

            RefreshTokenRequestDto requestDto = new RefreshTokenRequestDto(currentAccessToken, currentRefreshToken);
            retrofit2.Response<ApiResponse<AuthResponseDto>> refreshResponse =
                    plainApi.refreshToken(requestDto).execute();

            if (refreshResponse.isSuccessful()
                    && refreshResponse.body() != null
                    && refreshResponse.body().success
                    && refreshResponse.body().data != null) {

                AuthResponseDto newAuth = refreshResponse.body().data;
                // Lưu cặp token mới
                tokenManager.saveTokens(newAuth.accessToken, newAuth.refreshToken);
                tokenManager.saveUserInfo(newAuth);

                // Gửi lại request ban đầu với token mới
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAuth.accessToken)
                        .build();
            } else {
                // Refresh thất bại -> đăng xuất
                navigateToLogin();
                return null;
            }
        } catch (Exception e) {
            navigateToLogin();
            return null;
        }
    }

    /**
     * Đếm số lần response đã retry (để tránh lặp vô hạn).
     */
    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) {
            count++;
        }
        return count;
    }

    /**
     * Xóa token và chuyển về màn hình đăng nhập.
     */
    private void navigateToLogin() {
        tokenManager.clearTokens();
        try {
            // Import LoginActivity dynamically to avoid circular dependency
            Class<?> loginClass = Class.forName("com.webbansach.app.ui.auth_long.LoginActivity");
            Intent intent = new Intent(appContext, loginClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appContext.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
