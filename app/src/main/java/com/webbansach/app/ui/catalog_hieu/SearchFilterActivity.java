package com.webbansach.app.ui.catalog_hieu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.webbansach.app.core.network.ApiCallback;
import com.webbansach.app.core.network.ApiClient;
import com.webbansach.app.core.utils.Constants;
import com.webbansach.app.databinding.ActivityMh05FilterBinding;
import com.webbansach.app.models.CategoryDto;
import com.webbansach.app.models.AuthorDto;
import com.webbansach.app.models.PagedResponse;
import com.webbansach.app.models.ProductListItemDto;

import java.util.List;

/**
 * MH05 - Tìm kiếm & Lọc sản phẩm
 * Phụ trách: Nguyễn Trọng Hiếu
 *
 * Chức năng:
 * - Nhận EXTRA_CATEGORY_ID từ HomeActivity (nếu có) để lọc sẵn theo danh mục
 * - Tìm kiếm theo từ khóa (keyword)
 * - Lọc theo: Danh mục, Tác giả, Khoảng giá
 * - Sắp xếp: Giá tăng/giảm, Đánh giá, Giảm giá
 * - Phân trang (page, pageSize)
 * - Bấm sản phẩm -> truyền EXTRA_PRODUCT_ID sang BookDetailActivity (MH06)
 */
public class SearchFilterActivity extends AppCompatActivity {

    private ActivityMh05FilterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMh05FilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận categoryId nếu được truyền từ HomeActivity
        int categoryId = getIntent().getIntExtra(Constants.EXTRA_CATEGORY_ID, -1);

        // TODO [Hiếu]: Thiết kế giao diện tìm kiếm/lọc và xử lý sự kiện
        // Gợi ý:
        // - Gọi ApiClient.getApi(this).getCategories() để lấy danh sách danh mục
        // - Gọi ApiClient.getApi(this).getAuthors() để lấy danh sách tác giả
        // - Gọi ApiClient.getApi(this).getProducts(keyword, categoryId, ...) để tìm kiếm
    }
}
