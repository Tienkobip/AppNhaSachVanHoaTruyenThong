package com.webbansach.app.core.network;

import androidx.annotation.NonNull;

import com.webbansach.app.core.local.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp Interceptor tự động gắn Header Authorization: Bearer <accessToken>
 * vào mọi request gửi đi (trừ khi chưa đăng nhập).
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Nếu chưa có token thì gửi request gốc (cho các endpoint Anonymous)
        String token = tokenManager.getAccessToken();
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        // Gắn Bearer token vào Header
        Request authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(authorizedRequest);
    }
}
