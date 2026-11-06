package com.webbansach.app.core.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.webbansach.app.models.ApiResponse;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Callback thông minh tự động bóc tách ApiResponse<T> từ phản hồi API.
 * Cung cấp 2 phương thức gọn nhẹ: onSuccess() và onError().
 *
 * Cách sử dụng:
 *   ApiClient.getApi(this).getHomePage().enqueue(new ApiCallback<HomePageDataDto>() {
 *       @Override
 *       public void onSuccess(HomePageDataDto data, String message) {
 *           // Xử lý dữ liệu thành công
 *       }
 *       @Override
 *       public void onError(String errorMessage) {
 *           Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
 *       }
 *   });
 *
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public abstract class ApiCallback<T> implements Callback<ApiResponse<T>> {

    private static final String TAG = "ApiCallback";

    /**
     * Gọi khi API trả về thành công và success = true.
     * @param data Dữ liệu đã được parse sẵn từ trường "data" trong ApiResponse
     * @param message Thông báo từ server
     */
    public abstract void onSuccess(T data, String message);

    /**
     * Gọi khi có lỗi (HTTP error, network error, hoặc API trả success = false).
     * @param errorMessage Thông báo lỗi dễ hiểu cho người dùng
     */
    public abstract void onError(String errorMessage);

    @Override
    public void onResponse(@NonNull Call<ApiResponse<T>> call, @NonNull Response<ApiResponse<T>> response) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.success) {
                onSuccess(apiResponse.data, apiResponse.message != null ? apiResponse.message : "Thành công");
            } else {
                // API trả success = false (lỗi nghiệp vụ)
                String msg = apiResponse.message != null ? apiResponse.message : "Có lỗi xảy ra";
                if (apiResponse.errors != null && !apiResponse.errors.isEmpty()) {
                    msg = String.join(", ", apiResponse.errors);
                }
                onError(msg);
            }
        } else {
            // HTTP error (400, 401, 403, 404, 500, ...)
            String errorMsg = "Lỗi server: " + response.code();
            try {
                ResponseBody errorBody = response.errorBody();
                if (errorBody != null) {
                    String errorJson = errorBody.string();
                    // Thử parse errorBody thành ApiResponse để lấy message chi tiết
                    try {
                        ApiResponse<?> errorResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                        if (errorResponse != null && errorResponse.message != null) {
                            errorMsg = errorResponse.message;
                        }
                        if (errorResponse != null && errorResponse.errors != null && !errorResponse.errors.isEmpty()) {
                            errorMsg = String.join(", ", errorResponse.errors);
                        }
                    } catch (Exception parseEx) {
                        Log.w(TAG, "Không thể parse error body: " + errorJson);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Lỗi đọc error body", e);
            }
            onError(errorMsg);
        }
    }

    @Override
    public void onFailure(@NonNull Call<ApiResponse<T>> call, @NonNull Throwable t) {
        // Lỗi mạng (timeout, không có internet, DNS failed, ...)
        Log.e(TAG, "Network error: " + t.getMessage(), t);
        String errorMsg;
        if (t instanceof java.net.UnknownHostException) {
            errorMsg = "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.";
        } else if (t instanceof java.net.SocketTimeoutException) {
            errorMsg = "Kết nối quá thời gian. Vui lòng thử lại.";
        } else if (t instanceof javax.net.ssl.SSLHandshakeException) {
            errorMsg = "Lỗi kết nối bảo mật SSL. Vui lòng kiểm tra cấu hình server.";
        } else {
            errorMsg = "Lỗi kết nối: " + (t.getMessage() != null ? t.getMessage() : "Unknown error");
        }
        onError(errorMsg);
    }
}
