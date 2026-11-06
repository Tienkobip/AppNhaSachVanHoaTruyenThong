package com.webbansach.app.core.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.webbansach.app.models.AuthResponseDto;

/**
 * Quản lý Access Token, Refresh Token và thông tin người dùng đăng nhập
 * bằng SharedPreferences.
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public class TokenManager {

    private static final String PREF_NAME = "secure_token_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AVATAR_URL = "avatar_url";

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Lấy instance Singleton của TokenManager.
     */
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    /**
     * Lưu cặp Access Token và Refresh Token sau khi đăng nhập hoặc refresh.
     */
    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .apply();
    }

    /**
     * Lưu thông tin cá nhân của người dùng từ AuthResponseDto.
     */
    public void saveUserInfo(AuthResponseDto user) {
        if (user == null) return;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_USER_ID, user.customerId);
        if (user.fullName != null) editor.putString(KEY_FULL_NAME, user.fullName);
        if (user.email != null) editor.putString(KEY_EMAIL, user.email);
        if (user.phoneNumber != null) editor.putString(KEY_PHONE, user.phoneNumber);
        if (user.address != null) editor.putString(KEY_ADDRESS, user.address);
        if (user.dateOfBirth != null) editor.putString(KEY_DATE_OF_BIRTH, user.dateOfBirth);
        if (user.gender != null) editor.putString(KEY_GENDER, user.gender);
        if (user.avatarUrl != null) editor.putString(KEY_AVATAR_URL, user.avatarUrl);
        editor.apply();
    }

    // ===================== GETTER METHODS =====================

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUserFullName() {
        return prefs.getString(KEY_FULL_NAME, "");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getUserPhone() {
        return prefs.getString(KEY_PHONE, "");
    }

    public String getUserAddress() {
        return prefs.getString(KEY_ADDRESS, "");
    }

    public String getUserDateOfBirth() {
        return prefs.getString(KEY_DATE_OF_BIRTH, "");
    }

    public String getUserGender() {
        return prefs.getString(KEY_GENDER, "");
    }

    public String getUserAvatarUrl() {
        return prefs.getString(KEY_AVATAR_URL, "");
    }

    /**
     * Kiểm tra người dùng đã đăng nhập chưa (có access token hay không).
     */
    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }

    /**
     * Xóa toàn bộ token và thông tin người dùng (đăng xuất).
     */
    public void clearTokens() {
        prefs.edit().clear().apply();
    }
}
