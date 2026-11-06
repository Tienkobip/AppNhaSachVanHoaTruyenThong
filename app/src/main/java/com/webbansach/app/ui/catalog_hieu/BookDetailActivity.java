package com.webbansach.app.ui.catalog_hieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.core.utils.CurrencyUtils;
import com.webbansach.app.databinding.ActivityMh06DetailBinding;
import com.webbansach.app.models.AddToCartDto;
import com.webbansach.app.models.CartDto;
import com.webbansach.app.models.PagedResponse;
import com.webbansach.app.models.ProductDto;
import com.webbansach.app.models.ProductReviewDto;
import com.webbansach.app.ui.order_tien.CartActivity;

/**
 * MH06 - Chi tiết sách
 * Phụ trách: Nguyễn Trọng Hiếu
 *
 * Chức năng:
 * - Nhận EXTRA_PRODUCT_ID từ Intent
 * - Tải chi tiết sản phẩm (GET /api/products/{id})
 * - Hiển thị: Ảnh, Tên, Tác giả, Giá gốc/Giá giảm, Mô tả, Thông số
 * - Tải đánh giá (GET /api/products/{id}/reviews)
 * - Nút "Thêm vào giỏ hàng" -> POST /api/cart/items (AddToCartDto)
 * - Nút mở giỏ hàng -> CartActivity (MH07)
 */
public class BookDetailActivity extends AppCompatActivity {

    private ActivityMh06DetailBinding binding;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh06DetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = getIntent().getIntExtra(Constants.EXTRA_PRODUCT_ID, -1);

        // TODO [Hiếu]: Thiết kế giao diện chi tiết sách và xử lý sự kiện
        // Gợi ý:
        // ApiClient.getApi(this).getProductDetail(productId).enqueue(new ApiCallback<ProductDto>() { ... });
        // Nút thêm giỏ hàng:
        // ApiClient.getApi(this).addToCart(new AddToCartDto(productId, 1)).enqueue(new ApiCallback<CartDto>() { ... });
    }
}
