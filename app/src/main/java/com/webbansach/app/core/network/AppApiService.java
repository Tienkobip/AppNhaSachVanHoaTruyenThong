package com.webbansach.app.core.network;

import com.webbansach.app.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * Interface Retrofit chứa trọn bộ 34 Endpoints của WebApp.Api.
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public interface AppApiService {

    // ===================== AUTH & USER =====================

    /** MH02 - Đăng nhập JWT cho Mobile */
    @POST("api/Auth/mobile-login")
    Call<ApiResponse<AuthResponseDto>> mobileLogin(@Body CustomerLoginDto body);

    /** MH03 - Đăng ký tài khoản mới */
    @POST("api/Auth/register")
    Call<ApiResponse<AuthResponseDto>> register(@Body RegisterDto body);

    /** Cấp mới Access Token (Refresh Token Rotation) */
    @POST("api/Auth/refresh-token")
    Call<ApiResponse<AuthResponseDto>> refreshToken(@Body RefreshTokenRequestDto body);

    /** MH10 - Thu hồi Refresh Token khi đăng xuất */
    @POST("api/Auth/revoke-token")
    Call<ApiResponse<Boolean>> revokeToken(@Body RevokeTokenRequestDto body);

    /** MH10 - Lấy thông tin cá nhân */
    @GET("api/Auth/me")
    Call<ApiResponse<AuthResponseDto>> getMe();

    /** MH10 - Cập nhật thông tin cá nhân */
    @PUT("api/Auth/profile")
    Call<ApiResponse<AuthResponseDto>> updateProfile(@Body UpdateProfileDto body);

    /** MH10 - Đổi mật khẩu */
    @PUT("api/Auth/change-password")
    Call<ApiResponse<Boolean>> changePassword(@Body ChangePasswordDto body);

    // ===================== PRODUCTS & CATEGORIES =====================

    /** MH04 - Tải dữ liệu tổng hợp trang chủ (Banners, Danh mục, Sách mới, Bán chạy, Sách 0đ) */
    @GET("api/products/home")
    Call<ApiResponse<HomePageDataDto>> getHomePage();

    /** MH05 - Danh sách sản phẩm có bộ lọc & tìm kiếm */
    @GET("api/products")
    Call<ApiResponse<PagedResponse<ProductListItemDto>>> getProducts(
            @Query("keyword") String keyword,
            @Query("categoryId") Integer categoryId,
            @Query("authorId") Integer authorId,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("sortBy") String sortBy,
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize
    );

    /** Danh sách sản phẩm nổi bật */
    @GET("api/products/featured")
    Call<ApiResponse<List<ProductListItemDto>>> getFeaturedProducts(@Query("count") int count);

    /** MH06 - Chi tiết sản phẩm */
    @GET("api/products/{id}")
    Call<ApiResponse<ProductDto>> getProductDetail(@Path("id") int productId);

    /** MH06 - Danh sách đánh giá sản phẩm */
    @GET("api/products/{id}/reviews")
    Call<ApiResponse<PagedResponse<ProductReviewDto>>> getProductReviews(
            @Path("id") int productId,
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize
    );

    /** MH14 - Đăng đánh giá sản phẩm */
    @POST("api/products/reviews")
    Call<ApiResponse<ProductReviewDto>> createReview(@Body CreateReviewDto body);

    /** MH05 - Danh sách danh mục thể loại */
    @GET("api/categories")
    Call<ApiResponse<List<CategoryDto>>> getCategories();

    /** MH05 - Danh sách tác giả */
    @GET("api/categories/authors")
    Call<ApiResponse<List<AuthorDto>>> getAuthors();

    // ===================== CART =====================

    /** MH07 - Xem giỏ hàng */
    @GET("api/cart")
    Call<ApiResponse<CartDto>> getCart();

    /** MH06 - Thêm sản phẩm vào giỏ hàng */
    @POST("api/cart/items")
    Call<ApiResponse<CartDto>> addToCart(@Body AddToCartDto body);

    /** Cập nhật giỏ hàng (Body chứa productId + quantity) */
    @PUT("api/cart/items")
    Call<ApiResponse<CartDto>> updateCartItem(@Body UpdateCartItemDto body);

    /** MH07 - Cập nhật số lượng giỏ hàng RESTful */
    @PUT("api/cart/items/{productId}")
    Call<ApiResponse<CartDto>> updateCartItemQuantity(
            @Path("productId") int productId,
            @Body UpdateCartItemQuantityDto body
    );

    /** MH07 - Xóa sản phẩm khỏi giỏ hàng */
    @DELETE("api/cart/items/{productId}")
    Call<ApiResponse<CartDto>> removeCartItem(@Path("productId") int productId);

    /** Xóa sạch toàn bộ giỏ hàng */
    @DELETE("api/cart")
    Call<ApiResponse<Boolean>> clearCart();

    // ===================== ADDRESSES =====================

    /** MH11 - Lấy danh sách địa chỉ giao hàng */
    @GET("api/addresses")
    Call<ApiResponse<List<AddressDto>>> getAddresses();

    /** MH11 - Thêm địa chỉ mới */
    @POST("api/addresses")
    Call<ApiResponse<AddressDto>> createAddress(@Body CreateAddressDto body);

    /** MH11 - Cập nhật địa chỉ */
    @PUT("api/addresses/{id}")
    Call<ApiResponse<AddressDto>> updateAddress(@Path("id") int addressId, @Body UpdateAddressDto body);

    /** MH11 - Xóa địa chỉ */
    @DELETE("api/addresses/{id}")
    Call<ApiResponse<Boolean>> deleteAddress(@Path("id") int addressId);

    // ===================== ORDERS =====================

    /** MH09 - Kiểm tra mã giảm giá (Voucher) */
    @POST("api/orders/apply-voucher")
    Call<ApiResponse<VoucherApplyResultDto>> applyVoucher(@Body ApplyVoucherDto body);

    /** MH09 - Đặt hàng Checkout */
    @POST("api/orders/checkout")
    Call<ApiResponse<OrderDto>> checkout(@Body CreateOrderDto body);

    /** MH11 - Danh sách đơn hàng của tôi (lọc theo trạng thái tùy chọn) */
    @GET("api/orders/my-orders")
    Call<ApiResponse<List<OrderListItemDto>>> getMyOrders(@Query("status") Integer status);

    /** MH12 - Xem chi tiết đơn hàng */
    @GET("api/orders/{id}")
    Call<ApiResponse<OrderDto>> getOrderDetail(@Path("id") int orderId);

    /** MH12 - Hủy đơn hàng (chỉ khi trạng thái Pending) */
    @PUT("api/orders/{id}/cancel")
    Call<ApiResponse<Boolean>> cancelOrder(@Path("id") int orderId, @Body CancelOrderRequestDto body);

    // ===================== RETURN REQUESTS =====================

    /** MH13 - Tạo yêu cầu trả hàng */
    @POST("api/ReturnRequests")
    Call<ApiResponse<ReturnRequestSummaryDto>> createReturnRequest(@Body CreateReturnRequestDto body);

    /** MH13 - Danh sách yêu cầu trả hàng của tôi */
    @GET("api/ReturnRequests/my-returns")
    Call<ApiResponse<List<ReturnRequestSummaryDto>>> getMyReturnRequests();
}
