package com.webbansach.app.core.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.webbansach.app.models.AuthResponseDto;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

/**
 * Quản lý Access Token, Refresh Token và thông tin người dùng đăng nhập
 * bằng SharedPreferences.
 */
public class TokenManager {

    private static final String PREF_NAME = "secure_token_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_ACCESS_TOKEN_EXP = "access_token_expiration";
    private static final String KEY_USER_ID = "user_id_str";
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
        if (user.userId != null) editor.putString(KEY_USER_ID, user.userId);
        if (user.accessTokenExpiration != null) editor.putString(KEY_ACCESS_TOKEN_EXP, user.accessTokenExpiration);
        if (user.fullName != null) editor.putString(KEY_FULL_NAME, user.fullName);
        if (user.email != null) editor.putString(KEY_EMAIL, user.email);
        if (user.phoneNumber != null) editor.putString(KEY_PHONE, user.phoneNumber);
        if (user.address != null) editor.putString(KEY_ADDRESS, user.address);
        if (user.dateOfBirth != null) editor.putString(KEY_DATE_OF_BIRTH, user.dateOfBirth);
        if (user.gender != null) editor.putString(KEY_GENDER, user.gender);
        if (user.avatarUrl != null) editor.putString(KEY_AVATAR_URL, user.avatarUrl);
        editor.apply();
    }

    /**
     * Lưu toàn bộ phiên đăng nhập (Tokens + UserInfo) từ AuthResponseDto.
     */
    public void saveAuthSession(AuthResponseDto authResponse) {
        if (authResponse == null) return;
        if (authResponse.accessToken != null && authResponse.refreshToken != null) {
            saveTokens(authResponse.accessToken, authResponse.refreshToken);
        }
        saveUserInfo(authResponse);
    }

    // ===================== GETTER METHODS =====================

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
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
     * Kiểm tra người dùng có phiên đăng nhập hợp lệ trong máy hay không.
     * Phải có cả AccessToken (đúng định dạng 3 phần JWT) và RefreshToken.
     */
    public boolean isLoggedIn() {
        String accessToken = getAccessToken();
        String refreshToken = getRefreshToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            return false;
        }
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return false;
        }
        String[] parts = accessToken.split("\\.");
        return parts.length == 3;
    }

    /**
     * Kiểm tra xem Access Token hiện tại đã hết hạn (claim "exp") hay chưa.
     * @return true nếu không có token, lỗi định dạng hoặc đã hết hạn.
     */
    public boolean isAccessTokenExpired() {
        String accessToken = getAccessToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            return true;
        }
        try {
            String[] parts = accessToken.split("\\.");
            if (parts.length != 3) return true;
            byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            String payloadJson = new String(decodedBytes, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(payloadJson);
            long expSeconds = jsonObject.optLong("exp", 0L);
            if (expSeconds <= 0L) return true;
            long nowSeconds = System.currentTimeMillis() / 1000L;
            // Trừ hao 15 giây để chủ động refresh sớm
            return nowSeconds >= (expSeconds - 15L);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Xóa toàn bộ token và thông tin người dùng (đăng xuất).
     */
    public void clearTokens() {
        prefs.edit().clear().apply();
    }
}
