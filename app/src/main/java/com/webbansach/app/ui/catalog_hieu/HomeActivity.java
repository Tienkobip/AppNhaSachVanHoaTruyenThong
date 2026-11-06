package com.webbansach.app.ui.catalog_hieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.databinding.ActivityMh04HomeBinding;
import com.webbansach.app.models.HomePageDataDto;
import com.webbansach.app.ui.auth_long.ProfileActivity;
import com.webbansach.app.ui.order_tien.CartActivity;

/**
 * MH04 - Trang chủ (Home)
 * Phụ trách: Nguyễn Trọng Hiếu
 *
 * Chức năng:
 * - Gọi API GET /api/products/home để tải toàn bộ dữ liệu trang chủ
 * - Hiển thị: Banners (ViewPager/ImageSlider), Danh mục, Sách mới, Sách bán chạy, Tủ sách 0đ
 * - Bấm sản phẩm -> truyền EXTRA_PRODUCT_ID sang BookDetailActivity (MH06)
 * - Bấm danh mục -> truyền EXTRA_CATEGORY_ID sang SearchFilterActivity (MH05)
 * - Thanh bottom navigation: Trang chủ, Tìm kiếm (MH05), Giỏ hàng (MH07), Tài khoản (MH10)
 */
public class HomeActivity extends AppCompatActivity {

    private ActivityMh04HomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh04HomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO [Hiếu]: Thiết kế giao diện trang chủ và xử lý sự kiện
        // Gợi ý:
        // ApiClient.getApi(this).getHomePage().enqueue(new ApiCallback<HomePageDataDto>() {
        //     @Override
        //     public void onSuccess(HomePageDataDto data, String message) {
        //         // Hiển thị data.banners, data.categories, data.newProducts, data.bestSellerProducts, data.freeBooks
        //     }
        //     @Override
        //     public void onError(String errorMessage) {
        //         Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        //     }
        // });
    }
}
