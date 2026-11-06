package com.webbansach.app.ui.catalog_hieu;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.databinding.ActivityMh14ReviewBinding;
import com.webbansach.app.models.CreateReviewDto;
import com.webbansach.app.models.ProductReviewDto;

/**
 * MH14 - Viết đánh giá sản phẩm
 * Phụ trách: Nguyễn Trọng Hiếu
 *
 * Chức năng:
 * - Nhận EXTRA_PRODUCT_ID, EXTRA_ORDER_ID, EXTRA_PRODUCT_TITLE từ MH12
 * - Hiển thị tên sách đang đánh giá
 * - Chọn số sao (1-5), nhập bình luận
 * - Gửi đánh giá (POST /api/products/reviews)
 */
public class WriteReviewActivity extends AppCompatActivity {

    private ActivityMh14ReviewBinding binding;
    private int productId;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh14ReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = getIntent().getIntExtra(Constants.EXTRA_PRODUCT_ID, -1);
        orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);
        String productTitle = getIntent().getStringExtra(Constants.EXTRA_PRODUCT_TITLE);

        // TODO [Hiếu]: Thiết kế giao diện viết đánh giá và xử lý sự kiện
        // Gợi ý:
        // CreateReviewDto dto = new CreateReviewDto(productId, orderId, rating, comment);
        // ApiClient.getApi(this).createReview(dto).enqueue(new ApiCallback<ProductReviewDto>() { ... });
    }
}
