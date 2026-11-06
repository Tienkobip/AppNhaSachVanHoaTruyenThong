package com.webbansach.app.ui.order_tien;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.databinding.ActivityMh11AddressBinding;
import com.webbansach.app.models.AddressDto;
import com.webbansach.app.models.CreateAddressDto;
import com.webbansach.app.models.UpdateAddressDto;

import java.util.List;

/**
 * MH11 - Sổ địa chỉ giao hàng
 * Phụ trách: Nguyễn Việt Tiến
 *
 * Chức năng:
 * - Lấy danh sách địa chỉ (GET /api/addresses)
 * - Thêm địa chỉ mới (POST /api/addresses)
 * - Sửa địa chỉ (PUT /api/addresses/{id})
 * - Xóa địa chỉ (DELETE /api/addresses/{id})
 * - Nếu mở ở chế độ chọn (EXTRA_SELECT_ADDRESS_MODE = true):
 *   Bấm chọn -> trả về EXTRA_SELECTED_ADDRESS_JSON cho CheckoutActivity (MH08)
 */
public class AddressListActivity extends AppCompatActivity {

    private ActivityMh11AddressBinding binding;
    private boolean isSelectMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh11AddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isSelectMode = getIntent().getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS_MODE, false);

        // TODO [Tiến]: Thiết kế giao diện sổ địa chỉ và xử lý sự kiện
        // Gợi ý:
        // ApiClient.getApi(this).getAddresses().enqueue(new ApiCallback<List<AddressDto>>() { ... });
        // Khi user chọn 1 địa chỉ trong chế độ select:
        // if (isSelectMode) {
        //     Intent resultIntent = new Intent();
        //     resultIntent.putExtra(Constants.EXTRA_SELECTED_ADDRESS_JSON, new Gson().toJson(address));
        //     setResult(RESULT_OK, resultIntent);
        //     finish();
        // }
    }
}
