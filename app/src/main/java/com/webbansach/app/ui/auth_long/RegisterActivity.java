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
import com.webbansach.app.databinding.ActivityMh03RegisterBinding;
import com.webbansach.app.models.AuthResponseDto;
import com.webbansach.app.models.RegisterDto;
import com.webbansach.app.ui.catalog_hieu.HomeActivity;

/**
 * MH03 - Đăng ký tài khoản
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Nhập Họ tên, Email, SĐT, Mật khẩu, Xác nhận MK, Địa chỉ (tùy chọn)
 * - Validation phía Client khớp 100% với RegisterDtoValidator và IdentityOptions của Backend
 * - Gọi API POST /api/Auth/register
 * - Tự động đăng nhập và lưu token khi đăng ký thành công (nhánh isMobile=true của Backend)
 * - Chuyển đến HomeActivity (MH04) và xóa sạch Back Stack
 * - Điều hướng quay lại LoginActivity (MH02)
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityMh03RegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh03RegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
    }

    private void setupListeners() {
        // Tự động xóa thông báo lỗi khi người dùng gõ lại trên cả 6 ô
        binding.etFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilFullName.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

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

        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilPhone.setError(null);
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

        binding.etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilConfirmPassword.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilAddress.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Bắt phím Done trên bàn phím ảo ở ô etAddress
        binding.etAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performRegister();
                return true;
            }
            return false;
        });

        // Nút Đăng ký
        binding.btnRegister.setOnClickListener(v -> performRegister());

        // Điều hướng quay lại Đăng nhập
        binding.tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void performRegister() {
        String fullName = binding.etFullName.getText() != null
                ? binding.etFullName.getText().toString().trim()
                : "";
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim()
                : "";
        String phone = binding.etPhone.getText() != null
                ? binding.etPhone.getText().toString().trim()
                : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString()
                : "";
        String confirmPassword = binding.etConfirmPassword.getText() != null
                ? binding.etConfirmPassword.getText().toString()
                : "";
        String address = binding.etAddress.getText() != null
                ? binding.etAddress.getText().toString().trim()
                : "";

        // 1. Validation FullName
        if (fullName.isEmpty()) {
            binding.tilFullName.setError(getString(R.string.long_err_empty_full_name));
            binding.etFullName.requestFocus();
            return;
        }
        if (fullName.length() > 150) {
            binding.tilFullName.setError(getString(R.string.long_err_max_full_name));
            binding.etFullName.requestFocus();
            return;
        }

        // 2. Validation Email
        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.long_err_empty_email));
            binding.etEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.long_err_invalid_email));
            binding.etEmail.requestFocus();
            return;
        }

        // 3. Validation Phone
        if (phone.isEmpty()) {
            binding.tilPhone.setError(getString(R.string.long_err_empty_phone));
            binding.etPhone.requestFocus();
            return;
        }
        if (!phone.matches("^0\\d{9}$")) {
            binding.tilPhone.setError(getString(R.string.long_err_invalid_phone));
            binding.etPhone.requestFocus();
            return;
        }

        // 4. Validation Password (Tối thiểu 6 ký tự và phải chứa ít nhất 1 chữ số)
        if (password.isEmpty() || password.length() < 6 || !password.matches(".*\\d.*")) {
            binding.tilPassword.setError(getString(R.string.long_err_short_password));
            binding.etPassword.requestFocus();
            return;
        }

        // 5. Validation Confirm Password
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.setError(getString(R.string.long_err_empty_confirm_password));
            binding.etConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.long_err_mismatch_password));
            binding.etConfirmPassword.requestFocus();
            return;
        }

        // Ẩn bàn phím ảo
        hideKeyboard();

        // Bật loading
        setLoading(true);

        RegisterDto registerDto = new RegisterDto(
                fullName,
                email,
                phone,
                password,
                confirmPassword,
                address.isEmpty() ? null : address
        );

        ApiClient.getApi(this).register(registerDto).enqueue(new ApiCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto data, String message) {
                setLoading(false);

                // Backend với isMobile=true trả về luôn session token
                if (data != null && data.accessToken != null && !data.accessToken.isEmpty()) {
                    TokenManager.getInstance(RegisterActivity.this).saveAuthSession(data);
                    Toast.makeText(RegisterActivity.this, R.string.long_register_success, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Dự phòng nếu API chỉ báo thành công mà không trả token
                    Toast.makeText(RegisterActivity.this, R.string.long_register_success, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);

                if (errorMessage != null) {
                    if (errorMessage.contains("Email")) {
                        binding.tilEmail.setError(errorMessage);
                        binding.etEmail.requestFocus();
                    } else if (errorMessage.contains("Số điện thoại") || errorMessage.contains("phone")) {
                        binding.tilPhone.setError(errorMessage);
                        binding.etPhone.requestFocus();
                    }
                }

                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnRegister.setEnabled(false);
            binding.btnRegister.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.etFullName.setEnabled(false);
            binding.etEmail.setEnabled(false);
            binding.etPhone.setEnabled(false);
            binding.etPassword.setEnabled(false);
            binding.etConfirmPassword.setEnabled(false);
            binding.etAddress.setEnabled(false);
            binding.tvLogin.setEnabled(false);
        } else {
            binding.btnRegister.setEnabled(true);
            binding.btnRegister.setText(R.string.long_btn_register);
            binding.progressBar.setVisibility(View.GONE);
            binding.etFullName.setEnabled(true);
            binding.etEmail.setEnabled(true);
            binding.etPhone.setEnabled(true);
            binding.etPassword.setEnabled(true);
            binding.etConfirmPassword.setEnabled(true);
            binding.etAddress.setEnabled(true);
            binding.tvLogin.setEnabled(true);
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
