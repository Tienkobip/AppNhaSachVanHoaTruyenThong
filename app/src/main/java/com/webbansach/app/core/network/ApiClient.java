package com.webbansach.app.core.network;

import android.content.Context;

import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.core.utils.Constants;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit Client tích hợp:
 * - Bypass SSL tự ký cho localhost 10.0.2.2:7188 (dành cho môi trường dev)
 * - AuthInterceptor (tự động gắn Bearer token)
 * - TokenAuthenticator (tự động refresh token khi 401)
 * - HttpLoggingInterceptor (log request/response cho debug)
 *
 * Cách sử dụng từ bất kỳ Activity nào:
 *   ApiClient.getApi(this).getHomePage().enqueue(new ApiCallback<HomePageDataDto>() { ... });
 *
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public class ApiClient {

    private static Retrofit retrofit;
    private static AppApiService apiService;

    private ApiClient() {} // Singleton - không cho tạo instance

    /**
     * Lấy instance AppApiService (Singleton). Tự động khởi tạo Retrofit nếu chưa có.
     *
     * @param context Context của Activity/Application
     * @return AppApiService để gọi API
     */
    public static synchronized AppApiService getApi(Context context) {
        if (apiService == null) {
            TokenManager tokenManager = TokenManager.getInstance(context);

            // Logging interceptor - hiển thị request/response trong Logcat
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Xây dựng OkHttpClient với SSL bypass + Auth
            OkHttpClient client = getUnsafeOkHttpClientBuilder()
                    .addInterceptor(new AuthInterceptor(tokenManager))
                    .addInterceptor(loggingInterceptor)
                    .authenticator(new TokenAuthenticator(context))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(AppApiService.class);
        }
        return apiService;
    }

    /**
     * Tạo OkHttpClient.Builder đã bypass SSL (trust all certificates).
     * CHỈ DÙNG CHO MÔI TRƯỜNG PHÁT TRIỂN với localhost tự ký.
     * KHÔNG SỬ DỤNG TRONG PRODUCTION.
     */
    public static OkHttpClient.Builder getUnsafeOkHttpClientBuilder() {
        try {
            // Trust manager chấp nhận mọi certificate (bỏ qua SSL verification)
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi cấu hình SSL bypass: " + e.getMessage(), e);
        }
    }

    /**
     * Reset Retrofit instance (dùng khi cần khởi tạo lại, VD: sau khi đổi BASE_URL).
     */
    public static synchronized void reset() {
        retrofit = null;
        apiService = null;
    }
}
