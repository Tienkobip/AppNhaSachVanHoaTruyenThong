package com.webbansach.app.ui.auth_long;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.webbansach.app.R;
import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.core.utils.CurrencyUtils;
import com.webbansach.app.databinding.ActivityMh13ReturnBinding;
import com.webbansach.app.models.CreateReturnRequestDto;
import com.webbansach.app.models.OrderDto;
import com.webbansach.app.models.ReturnItemInputDto;
import com.webbansach.app.models.ReturnRequestSummaryDto;
import com.webbansach.app.ui.auth_long.adapter.ReturnHistoryAdapter;
import com.webbansach.app.ui.auth_long.adapter.ReturnProductSelectAdapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MH13 - Yêu cầu trả hàng & Lịch sử đổi trả của tôi
 * Phụ trách: Trần Duy Long
 *
 * Chức năng:
 * - Bảo vệ màn hình: Yêu cầu đăng nhập, đẩy về LoginActivity nếu chưa có phiên.
 * - Chế độ 1: orderId <= 0 (Mở từ ProfileActivity): Ẩn form tạo mới, chỉ xem danh sách phiếu trả hàng của tôi.
 * - Chế độ 2: orderId > 0 (Mở từ OrderHistoryActivity): Tải chi tiết đơn hàng (GET /api/orders/{id}),
 *   chọn sản phẩm và số lượng muốn trả, nhập lý do, thông tin ngân hàng nhận tiền hoàn,
 *   đính kèm tối đa 5 ảnh minh chứng (JPEG Base64 < 5MB), gửi yêu cầu (POST /api/ReturnRequests).
 * - Tải và hiển thị danh sách lịch sử yêu cầu đổi trả (GET /api/ReturnRequests/my-returns).
 */
public class ReturnOrderActivity extends AppCompatActivity {

    private ActivityMh13ReturnBinding binding;
    private int orderId = -1;

    private ReturnProductSelectAdapter returnProductAdapter;
    private ReturnHistoryAdapter returnHistoryAdapter;

    private final List<String> selectedImagesBase64 = new ArrayList<>();
    private final List<String> selectedImageFileNames = new ArrayList<>();
    private ActivityResultLauncher<String> pickImagesLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Chốt chặn bảo mật phiên đăng nhập
        if (!TokenManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityMh13ReturnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận orderId từ Intent
        orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);

        initViewsAndAdapters();
        initImagePickerLauncher();
        setupListeners();

        // 2. Xử lý chế độ hiển thị theo orderId
        if (orderId <= 0) {
            // Chế độ chỉ xem lịch sử (mở từ ProfileActivity)
            binding.toolbar.setTitle(R.string.long_return_history_only_title);
            binding.layoutCreateReturnForm.setVisibility(View.GONE);
            loadMyReturnRequests();
        } else {
            // Chế độ tạo yêu cầu trả hàng cho đơn orderId (mở từ OrderHistoryActivity)
            binding.toolbar.setTitle(R.string.long_return_title);
            binding.layoutCreateReturnForm.setVisibility(View.VISIBLE);

            // Điền sẵn tên chủ tài khoản từ TokenManager
            String fullName = TokenManager.getInstance(this).getUserFullName();
            if (fullName != null && !fullName.trim().isEmpty()) {
                binding.etBankHolder.setText(fullName.trim().toUpperCase());
            }

            loadOrderDetailForReturn(orderId);
            loadMyReturnRequests();
        }
    }

    private void initViewsAndAdapters() {
        // RecyclerView danh sách sản phẩm trong đơn để chọn trả
        returnProductAdapter = new ReturnProductSelectAdapter();
        binding.rvOrderItemsToReturn.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderItemsToReturn.setAdapter(returnProductAdapter);

        returnProductAdapter.setOnSelectionChangedListener(estimatedRefundAmount -> {
            String text = getString(R.string.long_return_estimated_refund) + CurrencyUtils.formatVnd(estimatedRefundAmount);
            binding.tvEstimatedRefund.setText(text);
        });

        // RecyclerView danh sách lịch sử yêu cầu trả hàng
        returnHistoryAdapter = new ReturnHistoryAdapter();
        binding.rvReturnHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReturnHistory.setAdapter(returnHistoryAdapter);
    }

    private void initImagePickerLauncher() {
        pickImagesLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
            if (uris != null && !uris.isEmpty()) {
                processSelectedImages(uris);
            }
        });
    }

    private void setupListeners() {
        // Nút Back trên toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Chọn ảnh minh chứng
        binding.btnSelectImages.setOnClickListener(v -> pickImagesLauncher.launch("image/*"));

        // Xóa ảnh đã chọn
        binding.btnClearImages.setOnClickListener(v -> clearSelectedImages());

        // Gửi yêu cầu trả hàng
        binding.btnSubmitReturn.setOnClickListener(v -> submitReturnRequest());
    }

    /**
     * Tải chi tiết đơn hàng (GET /api/orders/{id})
     */
    private void loadOrderDetailForReturn(int orderId) {
        setLoading(true);
        ApiClient.getApi(this).getOrderDetail(orderId).enqueue(new ApiCallback<OrderDto>() {
            @Override
            public void onSuccess(OrderDto order, String message) {
                setLoading(false);
                if (order == null) {
                    Toast.makeText(ReturnOrderActivity.this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hiển thị tóm tắt đơn hàng
                String orderCodeText = order.orderCode != null ? order.orderCode : String.valueOf(order.orderId);
                String info = "Đơn hàng: #" + orderCodeText + " • "
                        + (order.orderDate != null ? order.orderDate.split("T")[0] : "") + "\n"
                        + "Tổng tiền đơn: " + CurrencyUtils.formatVnd(order.totalAmount)
                        + " (" + (order.statusText != null ? order.statusText : "") + ")";
                binding.tvOrderInfo.setText(info);

                // Đổ danh sách sản phẩm vào adapter
                returnProductAdapter.setData(order.items);

                // Cập nhật số tiền hoàn dự kiến ban đầu
                String refundText = getString(R.string.long_return_estimated_refund)
                        + CurrencyUtils.formatVnd(returnProductAdapter.getEstimatedRefundAmount());
                binding.tvEstimatedRefund.setText(refundText);

                // Chỉ đơn hàng Completed (status = 3) mới được tạo yêu cầu đổi trả
                if (order.status != 3) {
                    binding.btnSubmitReturn.setEnabled(false);
                    Toast.makeText(ReturnOrderActivity.this,
                            "Chỉ đơn hàng Đã hoàn thành mới có thể gửi yêu cầu đổi trả.",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(ReturnOrderActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Xử lý danh sách ảnh người dùng chọn (tối đa 5 ảnh, resize & nén JPEG Base64 < 5MB)
     */
    private void processSelectedImages(List<Uri> uris) {
        selectedImagesBase64.clear();
        selectedImageFileNames.clear();

        int maxImages = 5;
        if (uris.size() > maxImages) {
            Toast.makeText(this, "Hệ thống chỉ lấy tối đa 5 ảnh đầu tiên", Toast.LENGTH_SHORT).show();
        }

        int count = Math.min(uris.size(), maxImages);
        for (int i = 0; i < count; i++) {
            Uri uri = uris.get(i);
            try (InputStream is = getContentResolver().openInputStream(uri)) {
                Bitmap originalBitmap = BitmapFactory.decodeStream(is);
                if (originalBitmap == null) continue;

                // Resize nếu cạnh lớn nhất > 1024px
                int maxDimension = 1024;
                int width = originalBitmap.getWidth();
                int height = originalBitmap.getHeight();
                Bitmap resizedBitmap = originalBitmap;

                if (width > maxDimension || height > maxDimension) {
                    float ratio = Math.min((float) maxDimension / width, (float) maxDimension / height);
                    int targetWidth = Math.round(width * ratio);
                    int targetHeight = Math.round(height * ratio);
                    resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);
                }

                // Nén sang JPEG quality 80 (đảm bảo magic bytes FF D8 FF và dung lượng < 5MB)
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] bytes = baos.toByteArray();

                String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
                selectedImagesBase64.add(base64);
                selectedImageFileNames.add("evidence_" + (i + 1) + ".jpg");

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi đọc file ảnh số " + (i + 1), Toast.LENGTH_SHORT).show();
            }
        }

        updateImagesCountUi();
    }

    private void clearSelectedImages() {
        selectedImagesBase64.clear();
        selectedImageFileNames.clear();
        updateImagesCountUi();
    }

    private void updateImagesCountUi() {
        int count = selectedImagesBase64.size();
        if (count > 0) {
            binding.tvSelectedImagesCount.setText(getString(R.string.long_return_images_selected, count));
            binding.tvSelectedImagesCount.setVisibility(View.VISIBLE);
            binding.btnClearImages.setVisibility(View.VISIBLE);
        } else {
            binding.tvSelectedImagesCount.setVisibility(View.GONE);
            binding.btnClearImages.setVisibility(View.GONE);
        }
    }

    /**
     * Validate dữ liệu và gửi yêu cầu đổi trả (POST /api/ReturnRequests)
     */
    private void submitReturnRequest() {
        // 1. Kiểm tra sản phẩm được chọn
        List<ReturnItemInputDto> selectedItems = returnProductAdapter.getSelectedReturnItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, R.string.long_err_return_no_item_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra lý do trả hàng
        String reason = binding.etReason.getText() != null ? binding.etReason.getText().toString().trim() : "";
        if (reason.isEmpty()) {
            binding.tilReason.setError(getString(R.string.long_err_return_empty_reason));
            binding.etReason.requestFocus();
            return;
        } else {
            binding.tilReason.setError(null);
        }

        String description = binding.etDescription.getText() != null
                ? binding.etDescription.getText().toString().trim()
                : "";

        // 3. Kiểm tra thông tin ngân hàng hoàn tiền
        String bankName = binding.etBankName.getText() != null ? binding.etBankName.getText().toString().trim() : "";
        String bankAccount = binding.etBankAccount.getText() != null ? binding.etBankAccount.getText().toString().trim() : "";
        String bankHolder = binding.etBankHolder.getText() != null ? binding.etBankHolder.getText().toString().trim() : "";

        boolean hasBankError = false;
        if (bankName.isEmpty()) {
            binding.tilBankName.setError("Vui lòng nhập tên ngân hàng");
            hasBankError = true;
        } else {
            binding.tilBankName.setError(null);
        }

        if (bankAccount.isEmpty()) {
            binding.tilBankAccount.setError("Vui lòng nhập số tài khoản");
            hasBankError = true;
        } else {
            binding.tilBankAccount.setError(null);
        }

        if (bankHolder.isEmpty()) {
            binding.tilBankHolder.setError("Vui lòng nhập họ và tên chủ tài khoản");
            hasBankError = true;
        } else {
            binding.tilBankHolder.setError(null);
        }

        if (hasBankError) {
            Toast.makeText(this, R.string.long_err_return_empty_bank, Toast.LENGTH_SHORT).show();
            return;
        }

        // Đóng gói DTO
        CreateReturnRequestDto dto = new CreateReturnRequestDto();
        dto.orderId = orderId;
        dto.reason = reason;
        dto.detailDescription = description.isEmpty() ? null : description;
        dto.bankName = bankName;
        dto.bankAccountNumber = bankAccount;
        dto.bankAccountHolder = bankHolder;
        dto.bankAccountInfo = bankName + " - " + bankAccount + " - " + bankHolder;
        dto.items = selectedItems;
        dto.evidenceImagesBase64 = selectedImagesBase64.isEmpty() ? null : new ArrayList<>(selectedImagesBase64);
        dto.evidenceImageFileNames = selectedImageFileNames.isEmpty() ? null : new ArrayList<>(selectedImageFileNames);

        setLoading(true);
        ApiClient.getApi(this).createReturnRequest(dto).enqueue(new ApiCallback<ReturnRequestSummaryDto>() {
            @Override
            public void onSuccess(ReturnRequestSummaryDto data, String message) {
                setLoading(false);
                Toast.makeText(ReturnOrderActivity.this, R.string.long_return_submit_success, Toast.LENGTH_SHORT).show();

                // Ẩn form tạo mới để tránh bấm gửi trùng lặp
                binding.layoutCreateReturnForm.setVisibility(View.GONE);
                binding.toolbar.setTitle(R.string.long_return_history_only_title);

                // Tải lại danh sách lịch sử trả hàng để phiếu mới xuất hiện ngay lập tức
                loadMyReturnRequests();
            }

            @Override
            public void onError(String errorMessage) {
                setLoading(false);
                Toast.makeText(ReturnOrderActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Tải danh sách lịch sử yêu cầu trả hàng của tôi (GET /api/ReturnRequests/my-returns)
     */
    private void loadMyReturnRequests() {
        ApiClient.getApi(this).getMyReturnRequests().enqueue(new ApiCallback<List<ReturnRequestSummaryDto>>() {
            @Override
            public void onSuccess(List<ReturnRequestSummaryDto> data, String message) {
                if (data != null && !data.isEmpty()) {
                    binding.tvEmptyReturnHistory.setVisibility(View.GONE);
                    binding.rvReturnHistory.setVisibility(View.VISIBLE);
                    returnHistoryAdapter.submitList(data);
                } else {
                    binding.rvReturnHistory.setVisibility(View.GONE);
                    binding.tvEmptyReturnHistory.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                binding.rvReturnHistory.setVisibility(View.GONE);
                binding.tvEmptyReturnHistory.setVisibility(View.VISIBLE);
                binding.tvEmptyReturnHistory.setText(errorMessage);
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (binding.progressBar != null) {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        binding.btnSubmitReturn.setEnabled(!isLoading);
        binding.btnSelectImages.setEnabled(!isLoading);
    }
}
