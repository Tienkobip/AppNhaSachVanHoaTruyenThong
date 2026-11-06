# CẨM NANG HƯỚNG DẪN KỸ THUẬT PHÁT TRIỂN ỨNG DỤNG ANDROID
### DỰ ÁN: NHÀ SÁCH VĂN HÓA TRUYỀN THỐNG (`com.webbansach.app`)
**Áp dụng:** Nhánh phát triển `branch-long`, `branch-hieu`, `branch-tien`  
**Mục tiêu:** Tập trung 100% vào giao diện (UI) và nghiệp vụ (Business Logic), trừu tượng hóa toàn bộ tầng mạng, bảo đảm **Zero Git Conflict** khi hợp nhất code.

---

## MỤC LỤC
1. [QUY TẮC BẤT DI BẤT DỊCH (VÙNG AN TOÀN & CHỐNG CONFLICT)](#1-quy-tắc-bất-di-bất-dịch-vùng-an-toàn--chống-conflict)
2. [HẠ TẦNG DÙNG CHUNG CẦN BIẾT](#2-hạ-tầng-dùng-chung-cần-biết)
3. [PHẦN 1: TRẦN DUY LONG – XÁC THỰC, TÀI KHOẢN & TRẢ HÀNG](#phần-1-trần-duy-long--xác-thực-tài-khoản--trả-hàng)
4. [PHẦN 2: NGUYỄN TRỌNG HIẾU – TRANG CHỦ, CATALOG, CHI TIẾT & ĐÁNH GIÁ](#phần-2-nguyễn-trọng-hiếu--trang-chủ-catalog-chi-tiết--đánh-giá)
5. [PHẦN 3: NGUYỄN VIỆT TIẾN – GIỎ HÀNG, THANH TOÁN (GPS), ĐỊA CHỈ & ĐƠN HÀNG](#phần-3-nguyễn-việt-tiến--giỏ-hàng-thanh-toán-gps-địa-chỉ--đơn-hàng)
6. [BẢNG TEST DATA & TRẠNG THÁI HỆ THỐNG](#bảng-test-data--trạng-thái-hệ-thống)

---

## 1. QUY TẮC BẤT DI BẤT DỊCH (VÙNG AN TOÀN & CHỐNG CONFLICT)

Để đảm bảo đến ngày 15/11/2026 khi merge cả 3 nhánh vào `main` không bị conflict hoặc gãy app, toàn bộ thành viên bắt buộc tuân thủ 2 danh sách sau:

### 1.1. DANH SÁCH ĐEN (CẤM CHỈNH SỬA / KHÓA CHẶT)
> **CẢNH BÁO QUAN TRỌNG:**
> Tuyệt đối KHÔNG sửa, KHÔNG thêm dòng, KHÔNG format lại các file sau:
> 1. `AndroidManifest.xml` (Đã đăng ký đủ 14 Activity, quyền Internet & GPS).
> 2. `app/build.gradle.kts`, `gradle/libs.versions.toml`, `settings.gradle.kts`.
> 3. Toàn bộ thư mục `com.webbansach.app.core` (`ApiClient`, `AppApiService`, `TokenManager`, `Constants`,...).
> 4. Toàn bộ thư mục `com.webbansach.app.models` (Toàn bộ 36 DTO đã hoàn chỉnh 100%).
> 5. `res/values/strings.xml`, `colors.xml`, `themes.xml`.

### 1.2. DANH SÁCH TRẮNG (VÙNG ĐƯỢC PHÉP THAO TÁC CỦA MỖI NGƯỜI)
Mỗi thành viên chỉ làm việc trong đúng phân vùng của mình:
- **Java Activity:** Chỉ code trong package `ui/<tên_thành_viên>/`.
- **Layout Activity:** Chỉ sửa các file `res/layout/activity_mh*.xml` được phân công.
- **RecyclerView Adapter & Item:**
  - Tạo class trong `ui/<tên_thành_viên>/adapter/`.
  - Tạo layout item bắt buộc có tiền tố tên: `item_long_*.xml`, `item_hieu_*.xml`, `item_tien_*.xml`.
- **File chuỗi riêng:** Nếu có text mới, khai báo vào `strings_long.xml`, `strings_hieu.xml`, `strings_tien.xml`.
- **Icon / Drawable mới:** Đặt tên có tiền tố: `ic_long_*.xml`, `bg_hieu_*.xml`, `ic_tien_*.xml`.

---

## 2. HẠ TẦNG DÙNG CHUNG CẦN BIẾT

Mọi thành viên không cần quan tâm Retrofit, OkHttp hay Gson được cấu hình ra sao. Chỉ cần nhớ các câu lệnh chuẩn sau:

### 2.1. Cách gọi API (Chỉ 1 mẫu duy nhất)
Từ bất kỳ `Activity` nào, gọi trực tiếp thông qua `ApiClient` và `ApiCallback`:
```java
ApiClient.getApi(this).<ten_ham_api>(...).enqueue(new ApiCallback<TenDto>() {
    @Override
    public void onSuccess(TenDto data, String message) {
        // Dữ liệu 'data' đã được parse sẵn và tự động chạy trên Main UI Thread
        // Cập nhật thẳng vào ViewBinding: binding.tvTitle.setText(...)
    }

    @Override
    public void onError(String errorMessage) {
        // errorMessage đã được lọc sẵn từ backend (kể cả lỗi HTTP 400 BadRequest)
        Toast.makeText(CurrentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
});
```

### 2.2. Hiển thị ảnh và định dạng tiền tệ
```java
// 1. Load ảnh sản phẩm (tự động nối BASE_URL nếu là link tương đối /images/...)
Glide.with(this)
     .load(CurrencyUtils.resolveImageUrl(item.mainImageUrl))
     .placeholder(android.R.drawable.ic_menu_gallery)
     .error(android.R.drawable.ic_delete)
     .into(binding.ivProductThumb);

// 2. Format giá tiền (Ví dụ: 157250 -> "157.250 đ")
binding.tvPrice.setText(CurrencyUtils.formatVnd(item.finalPrice));
```

### 2.3. Style giao diện chuẩn Fahasa / ViettelPost
- Màu đỏ chủ đạo: `@color/primary_red` (`#C92127`)
- Nền màn hình: `@color/bg_screen` (`#F0F2F5`)
- Nút bấm chính: `style="@style/Widget.App.Button.Primary"`
- Nút bấm viền: `style="@style/Widget.App.Button.Outlined"`
- Thẻ Card nổi bo góc: `style="@style/Widget.App.CardView"` hoặc `android:background="@drawable/bg_card_rounded"`
- Khung tìm kiếm viên thuốc: `android:background="@drawable/bg_search_bar"`

---

## PHẦN 1: TRẦN DUY LONG – XÁC THỰC, TÀI KHOẢN & TRẢ HÀNG

### 1.1. Phạm vi phụ trách
- **Thư mục code:** `com.webbansach.app.ui.auth_long`
- **Adapter & Item:** `ui/auth_long/adapter/` | `res/layout/item_long_*.xml`
- **File chuỗi:** `res/values/strings_long.xml`
- **5 Màn hình:**
  1. `SplashActivity.java` (`activity_mh01_splash.xml`) – MH01
  2. `LoginActivity.java` (`activity_mh02_login.xml`) – MH02
  3. `RegisterActivity.java` (`activity_mh03_register.xml`) – MH03
  4. `ProfileActivity.java` (`activity_mh10_profile.xml`) – MH10
  5. `ReturnOrderActivity.java` (`activity_mh13_return.xml`) – MH13

### 1.2. Các DTOs giao tiếp
| Tên DTO | Mục đích | Các trường chính |
|:---|:---|:---|
| `CustomerLoginDto` | Body đăng nhập | `emailOrPhone`, `password` |
| `RegisterDto` | Body đăng ký | `fullName`, `email`, `phoneNumber`, `password`, `confirmPassword`, `address` |
| `AuthResponseDto` | Nhận kết quả Login/Register/Profile | `accessToken`, `refreshToken`, `customerId`, `fullName`, `email`, `phoneNumber`, `address`, `avatarUrl` |
| `UpdateProfileDto` | Body sửa hồ sơ | `fullName`, `phoneNumber`, `dateOfBirth`, `address`, `gender`, `avatarBase64`, `avatarFileName` |
| `ChangePasswordDto` | Body đổi mật khẩu | `currentPassword`, `newPassword`, `confirmNewPassword` |
| `RevokeTokenRequestDto` | Body khi đăng xuất | `refreshToken` |
| `CreateReturnRequestDto` | Body tạo phiếu trả hàng | `orderId`, `reason`, `detailDescription`, `bankName`, `bankAccountNumber`, `bankAccountHolder`, `items`, `evidenceImagesBase64` |
| `ReturnItemInputDto` | Chi tiết sách muốn trả | `productId`, `quantity`, `note` |
| `ReturnRequestSummaryDto`| Danh sách phiếu trả hàng | `returnRequestId`, `orderCode`, `reason`, `status`, `statusText`, `refundAmount` |

### 1.3. Endpoints API gọi qua `ApiClient.getApi(this)`
```java
// 1. Đăng nhập Mobile (MH02)
ApiClient.getApi(this).mobileLogin(new CustomerLoginDto(emailOrPhone, password))

// 2. Đăng ký (MH03)
ApiClient.getApi(this).register(new RegisterDto(...))

// 3. Lấy thông tin cá nhân (MH10)
ApiClient.getApi(this).getMe()

// 4. Sửa hồ sơ cá nhân (MH10)
ApiClient.getApi(this).updateProfile(new UpdateProfileDto(...))

// 5. Đổi mật khẩu (MH10)
ApiClient.getApi(this).changePassword(new ChangePasswordDto(oldPass, newPass, confirmPass))

// 6. Đăng xuất (MH10)
ApiClient.getApi(this).revokeToken(new RevokeTokenRequestDto(tokenManager.getRefreshToken()))

// 7. Lấy chi tiết đơn để chọn hàng trả (MH13)
ApiClient.getApi(this).getOrderDetail(orderId)

// 8. Tạo yêu cầu trả hàng (MH13)
ApiClient.getApi(this).createReturnRequest(new CreateReturnRequestDto(...))

// 9. Xem danh sách yêu cầu trả hàng của tôi (MH13)
ApiClient.getApi(this).getMyReturnRequests()
```

### 1.4. Quản lý Đăng nhập với `TokenManager`
```java
TokenManager tokenManager = TokenManager.getInstance(this);

// Kiểm tra trạng thái đã đăng nhập chưa
if (tokenManager.isLoggedIn()) { ... }

// Lưu Token & User sau khi Login / Register thành công
tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken);
tokenManager.saveUserInfo(authResponse);

// Đọc thông tin hiển thị lên giao diện
String name = tokenManager.getUserFullName();
String email = tokenManager.getUserEmail();
String phone = tokenManager.getUserPhone();

// Đăng xuất: Xóa token
tokenManager.clearTokens();
```

### 1.5. Hằng số Intent Extras sử dụng
- `Constants.EXTRA_ORDER_ID`: Nhận ID đơn hàng truyền sang từ màn hình MH12 của Tiến để trả hàng:
  ```java
  int orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);
  ```

---

## PHẦN 2: NGUYỄN TRỌNG HIẾU – TRANG CHỦ, CATALOG, CHI TIẾT & ĐÁNH GIÁ

### 2.1. Phạm vi phụ trách
- **Thư mục code:** `com.webbansach.app.ui.catalog_hieu`
- **Adapter & Item:** `ui/catalog_hieu/adapter/` | `res/layout/item_hieu_*.xml`
- **File chuỗi:** `res/values/strings_hieu.xml`
- **4 Màn hình:**
  1. `HomeActivity.java` (`activity_mh04_home.xml`) – MH04
  2. `SearchFilterActivity.java` (`activity_mh05_filter.xml`) – MH05
  3. `BookDetailActivity.java` (`activity_mh06_detail.xml`) – MH06
  4. `WriteReviewActivity.java` (`activity_mh14_review.xml`) – MH14

### 2.2. Các DTOs giao tiếp
| Tên DTO | Mục đích | Các trường chính |
|:---|:---|:---|
| `HomePageDataDto` | Toàn bộ dữ liệu Trang chủ | `banners`, `categories`, `newProducts`, `bestSellerProducts`, `freeBooks` |
| `BannerDto` | Banner quảng cáo | `bannerId`, `title`, `imageUrl`, `targetUrl` |
| `CategoryDto` | Danh mục thể loại | `categoryId`, `categoryName`, `totalProducts` |
| `AuthorDto` | Tác giả | `authorId`, `authorName`, `biography` |
| `ProductListItemDto`| Thẻ sách rút gọn (List/Grid) | `productId`, `title`, `authorName`, `price`, `discountPercent`, `finalPrice`, `mainImageUrl`, `averageRating` |
| `ProductDto` | Toàn bộ chi tiết 1 cuốn sách | `productId`, `title`, `authorName`, `description`, `price`, `finalPrice`, `stockQuantity`, `imageUrls`, `publisher`, `publishYear`, `isbn`,... |
| `ProductReviewDto` | Đánh giá của khách | `reviewId`, `customerName`, `customerAvatarUrl`, `rating`, `comment`, `createdAt` |
| `CreateReviewDto` | Body gửi đánh giá mới | `productId`, `orderId`, `rating`, `comment` |
| `AddToCartDto` | Thêm vào giỏ từ chi tiết | `productId`, `quantity` |
| `PagedResponse<T>` | Phân trang danh sách | `items`, `totalCount`, `page`, `pageSize`, `totalPages` |

### 2.3. Endpoints API gọi qua `ApiClient.getApi(this)`
```java
// 1. Tải toàn bộ dữ liệu trang chủ trong 1 round-trip (MH04)
ApiClient.getApi(this).getHomePage()

// 2. Lấy danh sách danh mục (MH04, MH05)
ApiClient.getApi(this).getCategories()

// 3. Lấy danh sách tác giả (MH05)
ApiClient.getApi(this).getAuthors()

// 4. Tìm kiếm & Lọc sản phẩm có phân trang (MH05)
ApiClient.getApi(this).getProducts(
    keyword,     // String (VD: "Kiều")
    categoryId,  // Integer (hoặc null)
    authorId,    // Integer (hoặc null)
    minPrice,    // Double (hoặc null)
    maxPrice,    // Double (hoặc null)
    sortBy,      // "price-asc", "price-desc", "rating", "discount"
    page,        // int (VD: 1)
    pageSize     // int (VD: 10)
)

// 5. Xem chi tiết sách (MH06)
ApiClient.getApi(this).getProductDetail(productId)

// 6. Danh sách đánh giá của sản phẩm (MH06)
ApiClient.getApi(this).getProductReviews(productId, page, pageSize)

// 7. Thêm sách vào giỏ hàng từ màn hình chi tiết (MH06)
ApiClient.getApi(this).addToCart(new AddToCartDto(productId, 1))

// 8. Đăng đánh giá sản phẩm (MH14)
ApiClient.getApi(this).createReview(new CreateReviewDto(productId, orderId, rating, comment))
```

### 2.4. Hằng số Intent Extras sử dụng
- **Truyền đi:**
  - Bấm sách ở MH04/MH05 mở MH06:
    ```java
    Intent intent = new Intent(this, BookDetailActivity.class);
    intent.putExtra(Constants.EXTRA_PRODUCT_ID, item.productId);
    startActivity(intent);
    ```
  - Bấm danh mục ở MH04 mở MH05:
    ```java
    intent.putExtra(Constants.EXTRA_CATEGORY_ID, category.categoryId);
    ```
  - Bấm nút "Vào giỏ hàng" ở MH06 mở MH07 của Tiến:
    ```java
    startActivity(new Intent(this, CartActivity.class));
    ```
- **Nhận về:**
  - `BookDetailActivity`: Nhận `Constants.EXTRA_PRODUCT_ID`.
  - `WriteReviewActivity`: Nhận `Constants.EXTRA_PRODUCT_ID`, `Constants.EXTRA_ORDER_ID`, `Constants.EXTRA_PRODUCT_TITLE` (do MH12 của Tiến truyền qua).

---

## PHẦN 3: NGUYỄN VIỆT TIẾN – GIỎ HÀNG, THANH TOÁN (GPS), ĐỊA CHỈ & ĐƠN HÀNG

### 3.1. Phạm vi phụ trách
- **Thư mục code:** `com.webbansach.app.ui.order_tien`
- **Adapter & Item:** `ui/order_tien/adapter/` | `res/layout/item_tien_*.xml`
- **File chuỗi:** `res/values/strings_tien.xml`
- **5 Màn hình:**
  1. `CartActivity.java` (`activity_mh07_cart.xml`) – MH07
  2. `CheckoutActivity.java` (`activity_mh08_checkout.xml`) – MH08
  3. `OrderSuccessActivity.java` (`activity_mh09_success.xml`) – MH09
  4. `AddressListActivity.java` (`activity_mh11_address.xml`) – MH11
  5. `OrderHistoryActivity.java` (`activity_mh12_order_history.xml`) – MH12

### 3.2. Các DTOs giao tiếp
| Tên DTO | Mục đích | Các trường chính |
|:---|:---|:---|
| `CartDto` | Toàn bộ giỏ hàng | `cartId`, `items`, `totalAmount`, `totalItems` |
| `CartItemDto` | Từng cuốn sách trong giỏ | `productId`, `title`, `price`, `discountPercent`, `finalPrice`, `quantity`, `subtotal`, `mainImageUrl` |
| `UpdateCartItemQuantityDto` | Cập nhật số lượng | `quantity` (nếu `<= 0` backend tự xóa) |
| `AddressDto` | Địa chỉ nhận hàng | `addressId`, `receiverName`, `receiverPhone`, `detailAddress`, `isDefault`, `fullAddress` |
| `CreateAddressDto` | Thêm địa chỉ mới | `receiverName`, `receiverPhone`, `detailAddress`, `setAsDefault` |
| `UpdateAddressDto` | Sửa địa chỉ | `addressId`, `receiverName`, `receiverPhone`, `detailAddress`, `setAsDefault` |
| `ApplyVoucherDto` | Kiểm tra mã giảm giá | `code`, `currentOrderAmount` |
| `VoucherApplyResultDto` | Kết quả giảm giá | `isValid`, `errorMessage`, `discountAmount`, `finalAmount` |
| `CreateOrderDto` | Body đặt hàng Checkout | `receiverName`, `receiverPhone`, `detailAddress`, `notes`, `paymentMethod`, `couponCode`, `savedAddressId` |
| `OrderDto` | Chi tiết đơn hàng | `orderId`, `orderCode`, `orderDate`, `status`, `statusText`, `totalAmount`, `items`,... |
| `OrderListItemDto` | Thẻ đơn hàng trong danh sách | `orderId`, `orderCode`, `orderDate`, `status`, `statusText`, `totalAmount`, `firstProductTitle`, `firstProductImageUrl` |
| `CancelOrderRequestDto` | Body hủy đơn hàng | `cancelReason` |

### 3.3. Endpoints API gọi qua `ApiClient.getApi(this)`
```java
// 1. Giỏ hàng (MH07)
ApiClient.getApi(this).getCart()
ApiClient.getApi(this).updateCartItemQuantity(productId, new UpdateCartItemQuantityDto(newQty))
ApiClient.getApi(this).removeCartItem(productId)

// 2. Sổ địa chỉ (MH11)
ApiClient.getApi(this).getAddresses()
ApiClient.getApi(this).createAddress(new CreateAddressDto(name, phone, detail, setAsDefault))
ApiClient.getApi(this).updateAddress(id, new UpdateAddressDto(...))
ApiClient.getApi(this).deleteAddress(id)

// 3. Checkout (MH08)
ApiClient.getApi(this).applyVoucher(new ApplyVoucherDto(voucherCode, totalAmount))
ApiClient.getApi(this).checkout(createOrderDto)

// 4. Lịch sử & Chi tiết đơn (MH12)
ApiClient.getApi(this).getMyOrders(status) // status: null để lấy tất cả, hoặc 0, 1, 2, 3, 4, 5
ApiClient.getApi(this).getOrderDetail(orderId)
ApiClient.getApi(this).cancelOrder(orderId, new CancelOrderRequestDto(reason))
```

### 3.4. Tích hợp GPS Lấy địa chỉ hiện tại (MH08)
Đã có sẵn class `GpsLocationHelper`. Trong `CheckoutActivity`, chỉ cần gọi:
```java
GpsLocationHelper gpsHelper = new GpsLocationHelper(this);

binding.btnGpsAddress.setOnClickListener(v -> {
    gpsHelper.getCurrentAddress(new GpsLocationHelper.OnAddressListener() {
        @Override
        public void onAddressFound(String fullAddress) {
            // Tự động điền vào EditText địa chỉ nhận hàng
            binding.etReceiverAddress.setText(fullAddress);
            Toast.makeText(CheckoutActivity.this, "Đã lấy địa chỉ GPS thành công!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(String errorMessage) {
            Toast.makeText(CheckoutActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    });
});
```

### 3.5. Kết nối Intent liên màn hình của Tiến
1. **Từ MH08 mở MH11 để chọn địa chỉ:**
   ```java
   Intent intent = new Intent(this, AddressListActivity.class);
   intent.putExtra(Constants.EXTRA_SELECT_ADDRESS_MODE, true);
   startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
   ```
2. **Từ MH08 đặt hàng xong mở MH09:**
   ```java
   Intent intent = new Intent(this, OrderSuccessActivity.class);
   intent.putExtra(Constants.EXTRA_ORDER_ID, orderDto.orderId);
   intent.putExtra(Constants.EXTRA_ORDER_CODE, orderDto.orderCode);
   startActivity(intent);
   finish();
   ```
3. **Từ MH12 mở màn hình Trả hàng (MH13 của Long):**
   ```java
   Intent intent = new Intent(this, ReturnOrderActivity.class);
   intent.putExtra(Constants.EXTRA_ORDER_ID, order.orderId);
   startActivity(intent);
   ```
4. **Từ MH12 mở màn hình Đánh giá (MH14 của Hiếu):**
   ```java
   Intent intent = new Intent(this, WriteReviewActivity.class);
   intent.putExtra(Constants.EXTRA_ORDER_ID, order.orderId);
   intent.putExtra(Constants.EXTRA_PRODUCT_ID, item.productId);
   intent.putExtra(Constants.EXTRA_PRODUCT_TITLE, item.title);
   startActivity(intent);
   ```

---

## BẢNG TEST DATA & TRẠNG THÁI HỆ THỐNG

### 1. Tài khoản Test được Seed sẵn trong Backend
- **Email / Số điện thoại:** `jackacevn@gmail.com` hoặc `0896699703`
- **Mật khẩu:** `12345`

### 2. Mã giảm giá (Vouchers) mẫu
- `VANHOA10`: Giảm 25.000 đ cho đơn từ 200.000 đ
- `GIAM30K`: Giảm 30.000 đ cho đơn từ 150.000 đ

### 3. Mã Phương thức thanh toán (`PaymentMethod`)
- `0`: Thanh toán khi nhận hàng (COD)
- `1`: VNPAY
- `2`: Momo
- `3`: Chuyển khoản ngân hàng (BankTransfer)

### 4. Mã Trạng thái đơn hàng (`OrderStatus`)
- `0`: Chờ xử lý (`Pending`) — *Chỉ trạng thái này mới được phép HỦY ĐƠN*
- `1`: Đang xử lý (`Processing`)
- `2`: Đang giao hàng (`Shipping`)
- `3`: Đã hoàn thành (`Completed`) — *Chỉ trạng thái này mới được ĐÁNH GIÁ và TRẢ HÀNG*
- `4`: Đã hủy (`Cancelled`)
- `5`: Đã trả hàng (`Returned`)
