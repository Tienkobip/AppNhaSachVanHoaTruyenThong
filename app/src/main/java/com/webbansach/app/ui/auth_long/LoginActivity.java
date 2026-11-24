package com.webbansach.app.ui.auth_long;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.R;
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
 * - Có nút khám phá sách dành cho khách vãng lai (tvContinueGuest)
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityMh02LoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh02LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
    }

    private void setupListeners() {
        // Tự động xóa thông báo lỗi khi người dùng gõ lại
        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Hỗ trợ phím Done trên bàn phím ảo
        binding.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performLogin();
                return true;
            }
            return false;
        });

        // Nút Đăng nhập
        binding.btnLogin.setOnClickListener(v -> performLogin());

        // Điều hướng sang Đăng ký
        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Điều hướng khách vãng lai
        binding.tvContinueGuest.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void performLogin() {
        String emailOrPhone = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim()
                : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString()
                : "";

        // Validation phía Client
        if (emailOrPhone.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.long_err_empty_email_phone));
            binding.etEmail.requestFocus();
            return;
        }

        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(emailOrPhone).matches();
        boolean isPhone = emailOrPhone.matches("^0\\d{9}$");
        if (!isEmail && !isPhone) {
            binding.tilEmail.setError(getString(R.string.long_err_invalid_email_phone));
            binding.etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.long_err_empty_password));
            binding.etPassword.requestFocus();
            return;
        }

        // Ẩn bàn phím ảo
        hideKeyboard();

        // Bật loading và gọi API
        setLoading(true);

        CustomerLoginDto loginDto = new CustomerLoginDto(emailOrPhone, password);
        ApiClient.getApi(this).mobileLogin(loginDto).enqueue(new ApiCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto data, String message) {
                setLoading(false);
                if (data != null && data.accessToken != null && !data.accessToken.isEmpty()) {
                    // Lưu phiên đăng nhập vào TokenManager
                    TokenManager tokenManager = TokenManager.getInstance(LoginActivity.this);
                    tokenManager.saveAuthSession(data);

                    Toast.makeText(LoginActivity.this, R.string.long_login_success, Toast.LENGTH_SHORT).show();

                    // Điều hướng sang HomeActivity và xóa sạch Back Stack
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: Dữ liệu trả về không hợp lệ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnLogin.setEnabled(false);
            binding.btnLogin.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.etEmail.setEnabled(false);
            binding.etPassword.setEnabled(false);
            binding.tvRegister.setEnabled(false);
            binding.tvContinueGuest.setEnabled(false);
        } else {
            binding.btnLogin.setEnabled(true);
            binding.btnLogin.setText(R.string.long_btn_login);
            binding.progressBar.setVisibility(View.GONE);
            binding.etEmail.setEnabled(true);
            binding.etPassword.setEnabled(true);
            binding.tvRegister.setEnabled(true);
            binding.tvContinueGuest.setEnabled(true);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
