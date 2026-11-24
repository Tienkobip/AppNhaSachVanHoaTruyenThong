package com.webbansach.app.ui.auth_long;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.webbansach.app.R;
import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.CurrencyUtils;
import com.webbansach.app.databinding.ActivityMh10ProfileBinding;
import com.webbansach.app.models.AuthResponseDto;
import com.webbansach.app.models.ChangePasswordDto;
import com.webbansach.app.models.RevokeTokenRequestDto;
import com.webbansach.app.models.UpdateProfileDto;
import com.webbansach.app.ui.order_tien.AddressListActivity;
import com.webbansach.app.ui.order_tien.OrderHistoryActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * MH10 - Tài khoản / Hồ sơ cá nhân
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Bảo vệ màn hình (yêu cầu đăng nhập, đẩy về LoginActivity nếu chưa có phiên)
 * - Hiển thị thông tin cá nhân tức thì từ TokenManager và đồng bộ với Server (GET /api/Auth/me)
 * - Đổi ảnh đại diện trực tiếp hoặc qua Hộp thoại cập nhật hồ sơ (nén chuẩn JPEG Base64)
 * - Cập nhật hồ sơ cá nhân (PUT /api/Auth/profile)
 * - Đổi mật khẩu tài khoản (PUT /api/Auth/change-password)
 * - Đăng xuất an toàn (POST /api/Auth/revoke-token -> clearTokens -> LoginActivity)
 * - Điều hướng liên màn hình: Sổ địa chỉ (MH11), Lịch sử đơn hàng (MH12), Đổi trả hàng (MH13)
 */
public class ProfileActivity extends AppCompatActivity {

    private ActivityMh10ProfileBinding binding;
    private TokenManager tokenManager;
    private AuthResponseDto currentUser;
    private String pendingAvatarBase64 = null;

    // Launcher chọn ảnh đại diện từ thiết bị
    private ActivityResultLauncher<String> pickAvatarLauncher;
    private boolean isDirectAvatarUpdate = false;
    private TextView dialogAvatarStatusTv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Chốt chặn bảo mật phiên đăng nhập
        tokenManager = TokenManager.getInstance(this);
        if (!tokenManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        binding = ActivityMh10ProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initImagePickerLauncher();
        displayLocalProfile();
        setupListeners();
        loadProfileFromServer();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initImagePickerLauncher() {
        pickAvatarLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                processSelectedImage(uri);
            }
        });
    }

    private void setupListeners() {
        // Nút Back trên toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Bấm vào avatar để đổi ảnh đại diện trực tiếp
        binding.ivAvatar.setOnClickListener(v -> {
            isDirectAvatarUpdate = true;
            pickAvatarLauncher.launch("image/*");
        });

        // Bấm nút Sửa hồ sơ cá nhân
        binding.btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        // Bấm nút Đổi mật khẩu
        binding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Bấm nút Sổ địa chỉ (MH11)
        binding.btnAddressBook.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, AddressListActivity.class));
        });

        // Bấm nút Lịch sử đơn hàng (MH12)
        binding.btnOrderHistory.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrderHistoryActivity.class));
        });

        // Bấm nút Yêu cầu đổi trả của tôi (MH13)
        binding.btnMyReturns.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ReturnOrderActivity.class);
            startActivity(intent);
        });

        // Bấm nút Đăng xuất
        binding.btnLogout.setOnClickListener(v -> confirmLogout());
    }

    /**
     * Hiển thị dữ liệu tức thì từ SharedPreferences để người dùng không phải chờ mạng
     */
    private void displayLocalProfile() {
        String fullName = tokenManager.getUserFullName();
        binding.tvFullName.setText(fullName != null && !fullName.isEmpty() ? fullName : getString(R.string.long_profile_not_updated));

        String email = tokenManager.getUserEmail();
        binding.tvEmail.setText(email != null && !email.isEmpty() ? email : "");

        String phone = tokenManager.getUserPhone();
        binding.tvPhone.setText(phone != null && !phone.isEmpty() ? phone : getString(R.string.long_profile_not_updated));

        String dob = tokenManager.getUserDateOfBirth();
        formatAndSetDob(dob);

        String address = tokenManager.getUserAddress();
        if (address != null && !address.trim().isEmpty()) {
            binding.tvAddress.setText(getString(R.string.long_profile_address_prefix) + address);
            binding.tvAddress.setVisibility(View.VISIBLE);
        } else {
            binding.tvAddress.setText(getString(R.string.long_profile_address_prefix) + getString(R.string.long_profile_not_updated));
        }

        loadAvatarImage(tokenManager.getUserAvatarUrl());
    }

    private void formatAndSetDob(String rawDob) {
        if (rawDob != null && !rawDob.trim().isEmpty()) {
            String cleanDob = rawDob;
            if (rawDob.contains("T")) {
                cleanDob = rawDob.split("T")[0];
            } else if (rawDob.length() >= 10) {
                cleanDob = rawDob.substring(0, 10);
            }
            binding.tvDob.setText(getString(R.string.long_profile_dob_prefix) + cleanDob);
            binding.tvDob.setVisibility(View.VISIBLE);
        } else {
            binding.tvDob.setText(getString(R.string.long_profile_dob_prefix) + getString(R.string.long_profile_not_updated));
        }
    }

    private void loadAvatarImage(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            String fullUrl = CurrencyUtils.resolveImageUrl(avatarUrl);
            Glide.with(this)
                    .load(fullUrl)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(binding.ivAvatar);
        } else {
            binding.ivAvatar.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    /**
     * Tải thông tin mới nhất từ Server (GET /api/Auth/me)
     */
    private void loadProfileFromServer() {
        setLoading(true);
        ApiClient.getApi(this).getMe().enqueue(new ApiCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto data, String message) {
                setLoading(false);
                if (data != null) {
                    currentUser = data;
                    tokenManager.saveUserInfo(data);
                    displayLocalProfile();
                }
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                if (!tokenManager.isLoggedIn()) {
                    redirectToLogin();
                } else {
                    Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Xử lý file ảnh được chọn, resize và nén sang Base64 chuẩn JPEG
     */
    private void processSelectedImage(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            Bitmap originalBitmap = BitmapFactory.decodeStream(is);
            if (originalBitmap == null) {
                Toast.makeText(this, "Không thể đọc file ảnh đã chọn", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thu nhỏ nếu kích thước > 800px để tối ưu dung lượng và bảo đảm nhỏ hơn 5MB
            int maxDimension = 800;
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();
            Bitmap resizedBitmap = originalBitmap;

            if (width > maxDimension || height > maxDimension) {
                float ratio = Math.min((float) maxDimension / width, (float) maxDimension / height);
                int targetWidth = Math.round(width * ratio);
                int targetHeight = Math.round(height * ratio);
                resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
            }

            // Nén sang JPEG quality 85 (có magic bytes FF D8 FF)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
            byte[] imageBytes = baos.toByteArray();
            pendingAvatarBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            // Cập nhật preview cục bộ ngay lập tức
            binding.ivAvatar.setImageBitmap(resizedBitmap);

            if (isDirectAvatarUpdate) {
                // Tự động gọi API cập nhật hồ sơ với avatar mới
                performDirectAvatarUpload();
            } else if (dialogAvatarStatusTv != null) {
                dialogAvatarStatusTv.setText("Đã chọn ảnh mới (JPEG)");
                dialogAvatarStatusTv.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void performDirectAvatarUpload() {
        if (pendingAvatarBase64 == null) return;

        setLoading(true);
        String fullName = currentUser != null ? currentUser.fullName : tokenManager.getUserFullName();
        String phone = currentUser != null ? currentUser.phoneNumber : tokenManager.getUserPhone();
        String dob = currentUser != null ? currentUser.dateOfBirth : tokenManager.getUserDateOfBirth();
        String address = currentUser != null ? currentUser.address : tokenManager.getUserAddress();

        if (dob != null && dob.contains("T")) {
            dob = dob.split("T")[0];
        }

        UpdateProfileDto dto = new UpdateProfileDto(
                fullName,
                phone,
                (dob != null && !dob.trim().isEmpty()) ? dob.trim() : null,
                address,
                pendingAvatarBase64,
                "avatar.jpg"
        );

        ApiClient.getApi(this).updateProfile(dto).enqueue(new ApiCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto data, String message) {
                setLoading(false);
                pendingAvatarBase64 = null;
                if (data != null) {
                    currentUser = data;
                    tokenManager.saveUserInfo(data);
                    displayLocalProfile();
                }
                Toast.makeText(ProfileActivity.this, R.string.long_update_profile_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                loadProfileFromServer();
            }
        });
    }

    /**
     * Hộp thoại Sửa hồ sơ cá nhân
     */
    private void showEditProfileDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.long_dialog_edit_profile_title);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 12);

        // Họ và tên
        TextInputLayout tilName = new TextInputLayout(this);
        tilName.setHint("Họ và tên (*)");
        TextInputEditText etName = new TextInputEditText(this);
        etName.setText(currentUser != null && currentUser.fullName != null ? currentUser.fullName : tokenManager.getUserFullName());
        tilName.addView(etName);
        layout.addView(tilName);

        // Số điện thoại
        TextInputLayout tilPhone = new TextInputLayout(this);
        tilPhone.setHint("Số điện thoại");
        TextInputEditText etPhone = new TextInputEditText(this);
        etPhone.setText(currentUser != null && currentUser.phoneNumber != null ? currentUser.phoneNumber : tokenManager.getUserPhone());
        tilPhone.addView(etPhone);
        layout.addView(tilPhone);

        // Ngày sinh
        TextInputLayout tilDob = new TextInputLayout(this);
        tilDob.setHint(getString(R.string.long_hint_dob));
        TextInputEditText etDob = new TextInputEditText(this);
        etDob.setFocusable(false);
        etDob.setClickable(true);

        String currentDob = currentUser != null ? currentUser.dateOfBirth : tokenManager.getUserDateOfBirth();
        if (currentDob != null && currentDob.contains("T")) {
            currentDob = currentDob.split("T")[0];
        }
        etDob.setText(currentDob != null ? currentDob : "");

        etDob.setOnClickListener(v -> showDatePicker(etDob));
        tilDob.addView(etDob);
        layout.addView(tilDob);

        // Địa chỉ
        TextInputLayout tilAddress = new TextInputLayout(this);
        tilAddress.setHint("Địa chỉ nhận hàng");
        TextInputEditText etAddress = new TextInputEditText(this);
        etAddress.setText(currentUser != null && currentUser.address != null ? currentUser.address : tokenManager.getUserAddress());
        tilAddress.addView(etAddress);
        layout.addView(tilAddress);

        // Nút chọn ảnh đại diện trong dialog
        MaterialButton btnPickAvatar = new MaterialButton(this);
        btnPickAvatar.setText("Chọn ảnh đại diện mới");
        btnPickAvatar.setTextColor(getResources().getColor(R.color.primary_red));
        layout.addView(btnPickAvatar);

        dialogAvatarStatusTv = new TextView(this);
        dialogAvatarStatusTv.setTextSize(12);
        dialogAvatarStatusTv.setTextColor(getResources().getColor(R.color.primary_red));
        dialogAvatarStatusTv.setVisibility(pendingAvatarBase64 != null ? View.VISIBLE : View.GONE);
        dialogAvatarStatusTv.setText(pendingAvatarBase64 != null ? "Đã có ảnh mới chờ lưu" : "");
        layout.addView(dialogAvatarStatusTv);

        btnPickAvatar.setOnClickListener(v -> {
            isDirectAvatarUpdate = false;
            pickAvatarLauncher.launch("image/*");
        });

        builder.setView(layout);
        builder.setPositiveButton("Lưu thay đổi", null); // Set null để tự can thiệp validation
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
            dialogAvatarStatusTv = null;
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String dob = etDob.getText() != null ? etDob.getText().toString().trim() : "";
            String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

            if (name.isEmpty()) {
                tilName.setError(getString(R.string.long_err_empty_full_name));
                return;
            }
            if (name.length() > 150) {
                tilName.setError(getString(R.string.long_err_max_full_name));
                return;
            }

            if (!phone.isEmpty() && !phone.matches("^0\\d{9}$")) {
                tilPhone.setError(getString(R.string.long_err_invalid_phone));
                return;
            }

            dialog.dismiss();
            dialogAvatarStatusTv = null;
            executeProfileUpdate(name, phone, dob, address);
        });
    }

    private void showDatePicker(EditText etDob) {
        Calendar calendar = Calendar.getInstance();
        int year = 2000;
        int month = 0;
        int day = 1;

        String currentText = etDob.getText().toString().trim();
        if (currentText.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            String[] parts = currentText.split("-");
            try {
                year = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1;
                day = Integer.parseInt(parts[2]);
            } catch (Exception ignored) {}
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d);
            etDob.setText(selectedDate);
        }, year, month, day);

        // Chặn không cho chọn ngày trong tương lai (LessThan(DateTime.UtcNow))
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 86400000L);
        datePickerDialog.show();
    }

    private void executeProfileUpdate(String fullName, String phone, String dob, String address) {
        setLoading(true);

        String cleanDob = (dob != null && !dob.trim().isEmpty()) ? dob.trim() : null;
        UpdateProfileDto dto = new UpdateProfileDto(
                fullName,
                phone,
                cleanDob,
                address,
                pendingAvatarBase64,
                pendingAvatarBase64 != null ? "avatar.jpg" : null
        );

        ApiClient.getApi(this).updateProfile(dto).enqueue(new ApiCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto data, String message) {
                setLoading(false);
                pendingAvatarBase64 = null;
                if (data != null) {
                    currentUser = data;
                    tokenManager.saveUserInfo(data);
                    displayLocalProfile();
                }
                Toast.makeText(ProfileActivity.this, R.string.long_update_profile_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Hộp thoại Đổi mật khẩu
     */
    private void showChangePasswordDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.long_dialog_change_pwd_title);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 12);

        TextInputLayout tilCurrent = new TextInputLayout(this);
        tilCurrent.setHint(getString(R.string.long_hint_current_password));
        tilCurrent.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        TextInputEditText etCurrent = new TextInputEditText(this);
        etCurrent.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilCurrent.addView(etCurrent);
        layout.addView(tilCurrent);

        TextInputLayout tilNew = new TextInputLayout(this);
        tilNew.setHint(getString(R.string.long_hint_new_password));
        tilNew.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        TextInputEditText etNew = new TextInputEditText(this);
        etNew.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilNew.addView(etNew);
        layout.addView(tilNew);

        TextInputLayout tilConfirm = new TextInputLayout(this);
        tilConfirm.setHint(getString(R.string.long_hint_confirm_new_password));
        tilConfirm.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        TextInputEditText etConfirm = new TextInputEditText(this);
        etConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilConfirm.addView(etConfirm);
        layout.addView(tilConfirm);

        builder.setView(layout);
        builder.setPositiveButton("Đổi mật khẩu", null);
        builder.setNegativeButton("Hủy", (d, w) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String currentPwd = etCurrent.getText() != null ? etCurrent.getText().toString() : "";
            String newPwd = etNew.getText() != null ? etNew.getText().toString() : "";
            String confirmPwd = etConfirm.getText() != null ? etConfirm.getText().toString() : "";

            if (currentPwd.isEmpty()) {
                tilCurrent.setError("Vui lòng nhập mật khẩu hiện tại");
                return;
            }
            if (newPwd.isEmpty() || newPwd.length() < 6 || !newPwd.matches(".*\\d.*")) {
                tilNew.setError(getString(R.string.long_err_short_password));
                return;
            }
            if (newPwd.equals(currentPwd)) {
                tilNew.setError(getString(R.string.long_err_same_old_password));
                return;
            }
            if (!newPwd.equals(confirmPwd)) {
                tilConfirm.setError(getString(R.string.long_err_mismatch_password));
                return;
            }

            dialog.dismiss();
            executeChangePassword(currentPwd, newPwd, confirmPwd);
        });
    }

    private void executeChangePassword(String oldPass, String newPass, String confirmPass) {
        setLoading(true);
        ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass, confirmPass);

        ApiClient.getApi(this).changePassword(dto).enqueue(new ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data, String message) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, R.string.long_change_pwd_success_relogin, Toast.LENGTH_LONG).show();
                performLocalLogoutAndRedirect();
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void performLocalLogoutAndRedirect() {
        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken != null && !refreshToken.isEmpty()) {
            ApiClient.getApi(this).revokeToken(new RevokeTokenRequestDto(refreshToken)).enqueue(new ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data, String message) {
                    finalizeLogoutDirectly();
                }

                @Override
                public void onError(String errorMessage) {
                    finalizeLogoutDirectly();
                }
            });
        } else {
            finalizeLogoutDirectly();
        }
    }

    private void finalizeLogoutDirectly() {
        tokenManager.clearTokens();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Xác nhận đăng xuất
     */
    private void confirmLogout() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.long_logout_confirm_title)
                .setMessage(R.string.long_logout_confirm_msg)
                .setPositiveButton("Đăng xuất", (dialog, which) -> executeLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeLogout() {
        setLoading(true);
        String refreshToken = tokenManager.getRefreshToken();

        if (refreshToken != null && !refreshToken.isEmpty()) {
            ApiClient.getApi(this).revokeToken(new RevokeTokenRequestDto(refreshToken)).enqueue(new ApiCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data, String message) {
                    finalizeLogout();
                }

                @Override
                public void onError(String errorMessage) {
                    finalizeLogout();
                }
            });
        } else {
            finalizeLogout();
        }
    }

    private void finalizeLogout() {
        setLoading(false);
        tokenManager.clearTokens();
        Toast.makeText(ProfileActivity.this, R.string.long_logout_success, Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private void setLoading(boolean isLoading) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        binding.btnEditProfile.setEnabled(!isLoading);
        binding.btnChangePassword.setEnabled(!isLoading);
        binding.btnAddressBook.setEnabled(!isLoading);
        binding.btnOrderHistory.setEnabled(!isLoading);
        binding.btnMyReturns.setEnabled(!isLoading);
        binding.btnLogout.setEnabled(!isLoading);
    }
}
