package com.webbansach.app.core.utils;

/**
 * Chứa các hằng số dùng chung toàn bộ dự án.
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public final class Constants {

    private Constants() {} // Không cho tạo instance

    // ===================== CẤU HÌNH MẠNG =====================
    /** Base URL của WebApp.Api (dành cho Android Emulator trỏ về localhost máy host) */
    public static final String BASE_URL = "https://10.0.2.2:7188/";

    // ===================== KHÓA INTENT EXTRA DÙNG CHUNG =====================
    /** ID sản phẩm (int) - dùng khi chuyển từ danh sách sang chi tiết sách */
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    /** Tên sản phẩm (String) - dùng khi hiển thị tên sách ở màn hình đánh giá */
    public static final String EXTRA_PRODUCT_TITLE = "extra_product_title";

    /** ID danh mục (int) - dùng khi lọc sản phẩm theo thể loại */
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";

    /** ID tác giả (int) - dùng khi lọc sản phẩm theo tác giả */
    public static final String EXTRA_AUTHOR_ID = "extra_author_id";

    /** ID đơn hàng (int) - dùng khi xem chi tiết đơn, trả hàng, đánh giá */
    public static final String EXTRA_ORDER_ID = "extra_order_id";

    /** Mã đơn hàng (String) - dùng ở màn hình đặt hàng thành công */
    public static final String EXTRA_ORDER_CODE = "extra_order_code";

    /** Chế độ chọn địa chỉ (boolean) - true khi mở AddressListActivity từ Checkout */
    public static final String EXTRA_SELECT_ADDRESS_MODE = "extra_select_address_mode";

    /** JSON địa chỉ đã chọn (String) - trả về từ AddressListActivity cho Checkout */
    public static final String EXTRA_SELECTED_ADDRESS_JSON = "extra_selected_address_json";
}
