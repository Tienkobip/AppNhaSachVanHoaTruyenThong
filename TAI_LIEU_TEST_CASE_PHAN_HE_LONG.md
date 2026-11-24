# TÀI LIỆU KỊCH BẢN KIỂM THỬ THỦ CÔNG (TEST CASE)
## PHÂN HỆ XÁC THỰC VÀ BẢO MẬT TÀI KHOẢN (TRẦN DUY LONG)
### Ứng dụng Di động: Nhà Sách Văn Hóa (`com.webbansach.app`)

---

## 1. HƯỚNG DẪN CHUẨN BỊ MÔI TRƯỜNG TRƯỚC KHI TEST

### 1.1. Cách chạy Backend WebApp.Api ở cổng HTTPS 7188
- Mở terminal hoặc PowerShell tại thư mục source code của dự án `WebApp.Api`.
- Thực thi lệnh:
  ```bash
  dotnet run --launch-profile "https"
  ```
- Xác nhận cổng lắng nghe HTTPS: `https://localhost:7188` (Android Emulator kết nối qua: `https://10.0.2.2:7188`).
- Kiểm tra giao diện tài liệu API tại: `https://localhost:7188/swagger`.

### 1.2. Thông tin tài khoản Seed mẫu (Seed Data)
- **Email:** `jackacevn@gmail.com`
- **Số điện thoại:** `0896699703`
- **Mật khẩu:** `12345`
- **Họ và tên:** Trần Duy Long
- **Quyền hạn:** Khách hàng (`Customer`)

### 1.3. Cách xóa dữ liệu app (Clear Storage) hoặc đăng xuất trên máy ảo Android
- **Cách 1:** Vào Cài đặt Android -> Ứng dụng -> Nhà Sách Văn Hóa -> Bộ nhớ và bộ nhớ đệm -> Xóa bộ nhớ lưu trữ (Clear Storage).
- **Cách 2:** Chạy lệnh ADB:
  ```bash
  adb shell pm clear com.webbansach.app
  ```

---

## 2. BẢNG TEST CASES CHI TIẾT CHO MÀN HÌNH MH02 — ĐĂNG NHẬP (`LoginActivity`)

| Mã TC | Tên kịch bản | Điều kiện tiên quyết | Các bước thực hiện | Kết quả mong đợi | Kết quả thực tế (Pass/Fail) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-MH02-01** | Bỏ trống cả Email/SĐT và Mật khẩu rồi bấm Đăng nhập | Đang ở màn hình `LoginActivity` | 1. Để trống cả 2 ô nhập liệu.<br>2. Bấm nút "ĐĂNG NHẬP". | Báo lỗi trên ô nhập (`tilEmail`: *"Vui lòng nhập Email hoặc Số điện thoại"*), con trỏ focus vào `etEmail`, không gọi API. | **Pass** |
| **TC-MH02-02** | Nhập sai định dạng Email và Số điện thoại | Đang ở màn hình `LoginActivity` | 1. Nhập chuỗi sai định dạng (không phải Email hợp lệ và không phải SĐT 10 số bắt đầu bằng 0, VD: `"abc123"` hoặc `"09123"`).<br>2. Nhập mật khẩu bất kỳ (VD: `"12345"`).<br>3. Bấm "ĐĂNG NHẬP". | Báo lỗi định dạng trên `tilEmail`: *"Email hoặc Số điện thoại không đúng định dạng"*, không gọi API. | **Pass** |
| **TC-MH02-03** | Nhập đúng Email nhưng bỏ trống Mật khẩu | Đang ở màn hình `LoginActivity` | 1. Nhập: `jackacevn@gmail.com`.<br>2. Để trống ô Mật khẩu.<br>3. Bấm "ĐĂNG NHẬP". | Báo lỗi trên `tilPassword`: *"Mật khẩu không được để trống"*, con trỏ focus vào `etPassword`, không gọi API. | **Pass** |
| **TC-MH02-04** | Đăng nhập sai mật khẩu | Backend đang chạy cổng 7188, có kết nối mạng | 1. Nhập: `jackacevn@gmail.com`.<br>2. Nhập mật khẩu sai: `sai_mat_khau`.<br>3. Bấm "ĐĂNG NHẬP". | Nút bấm bị khóa khi đang tải (`loading=true`, hiện `progressBar`), sau đó mở khóa và hiện Toast thông báo từ Server (kèm số lần thử còn lại trước khi khóa tài khoản). | **Pass** |
| **TC-MH02-05** | Đăng nhập thành công bằng Email | Backend đang chạy, tài khoản có trong DB | 1. Nhập Email: `jackacevn@gmail.com`.<br>2. Nhập Mật khẩu: `12345`.<br>3. Bấm "ĐĂNG NHẬP" (hoặc bấm Done trên bàn phím). | Lưu `accessToken`, `refreshToken`, `userInfo` vào SharedPreferences, hiện Toast thành công, chuyển sang `HomeActivity` (MH04). Bấm nút Back không quay lại màn hình Login. | **Pass** |
| **TC-MH02-06** | Đăng nhập thành công bằng Số điện thoại | Backend đang chạy, tài khoản có SĐT trong DB | 1. Nhập Số điện thoại: `0896699703`.<br>2. Nhập Mật khẩu: `12345`.<br>3. Bấm "ĐĂNG NHẬP". | Thành công, lưu phiên vào `TokenManager` và vào `HomeActivity` (MH04). | **Pass** |
| **TC-MH02-07** | Bấm chữ "Đăng ký ngay" (`tvRegister`) | Đang ở màn hình `LoginActivity` | 1. Bấm vào dòng chữ "Đăng ký ngay" (`tvRegister`) phía dưới form. | Mở màn hình `RegisterActivity` (MH03). Giữ nguyên Back Stack: Bấm nút Back sẽ quay lại `LoginActivity`. | **Pass** |
| **TC-MH02-08** | Bấm "Khám phá sách không cần đăng nhập" (`tvContinueGuest`) | Đang ở màn hình `LoginActivity` | 1. Bấm vào liên kết "Khám phá sách không cần đăng nhập" (`tvContinueGuest`). | Mở thẳng `HomeActivity` (MH04) ở chế độ khách (Guest). Đóng `LoginActivity` (`finish()`). | **Pass** |

---

## 3. BẢNG TEST CASES CHI TIẾT CHO MÀN HÌNH MH01 — KHỞI ĐỘNG (`SplashActivity`)

| Mã TC | Tên kịch bản | Điều kiện tiên quyết | Các bước thực hiện | Kết quả mong đợi | Kết quả thực tế (Pass/Fail) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-MH01-01** | Mở app lần đầu tiên (hoặc sau khi Clear Storage / chưa đăng nhập) | Ứng dụng mới cài đặt hoặc vừa xóa dữ liệu (`isLoggedIn == false`) | 1. Mở ứng dụng từ màn hình Home của thiết bị.<br>2. Quan sát logo Splash ~1.2 giây.<br>3. Chờ chuyển màn hình. | Hiển thị logo Splash ~1.2 giây rồi tự động chuyển sang `LoginActivity` (MH02). Bấm Back tại `LoginActivity` sẽ thoát app (không quay lại Splash). | **Pass** |
| **TC-MH01-02** | Mở lại app sau khi đã đăng nhập thành công ở MH02 (Token còn hạn) | Đã đăng nhập trước đó bằng `jackacevn@gmail.com`, Token còn hạn | 1. Đóng hẳn ứng dụng trong danh sách đa nhiệm.<br>2. Mở lại app từ màn hình chính. | Splash tự động gọi `GET /api/Auth/me`, đồng bộ thông tin user và chuyển thẳng vào `HomeActivity` (MH04), không bắt đăng nhập lại. | **Pass** |
| **TC-MH01-03** | Trường hợp Access Token hết hạn (hoặc bị sửa payload) nhưng Refresh Token trong máy vẫn hợp lệ | Access Token đã quá 30 phút, Refresh Token 14 ngày vẫn còn hạn | 1. Mở app khi Access Token đã quá hạn.<br>2. Splash gọi `getMe()` nhận mã 401. | Khi mở app, `SplashActivity` gọi `getMe()` nhận 401 -> `TokenAuthenticator` tự động gọi `/api/Auth/refresh-token` lấy cặp token mới -> `getMe()` thành công -> Vào thẳng `HomeActivity`. | **Pass** |
| **TC-MH01-04** | Trường hợp cả Access Token và Refresh Token đều không hợp lệ / bị thu hồi trên Server | Đã đổi mật khẩu trên Web hoặc token quá hạn 14 ngày | 1. Mở app tại Splash.<br>2. `getMe()` bị 401 -> `TokenAuthenticator` refresh-token thất bại. | Mở app tại Splash -> `getMe()` bị 401 -> Refresh thất bại -> Tự động xóa sạch token cũ và đẩy về `LoginActivity` (MH02). | **Pass** |
| **TC-MH01-05** | Bấm nút Back thoát ứng dụng ngay trong 1 giây đầu khi đang ở màn hình Splash | Ứng dụng đang trong thời gian đếm lùi khởi động 1.2 giây | 1. Nhấp mở app.<br>2. Bấm phím Back ngay trong vòng dưới 1 giây. | App thoát êm, không bị tự động bật `LoginActivity` hay `HomeActivity` đè lên màn hình chính của điện thoại sau đó (nhờ `removeCallbacksAndMessages` trong `onDestroy`). | **Pass** |

---

## 4. BẢNG TEST CASES CHI TIẾT CHO MÀN HÌNH MH03 — ĐĂNG KÝ TÀI KHOẢN (`RegisterActivity`)

| Mã TC | Tên kịch bản | Điều kiện tiên quyết | Các bước thực hiện | Kết quả mong đợi | Kết quả thực tế (Pass/Fail) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-MH03-01** | Bỏ trống Họ và tên rồi bấm Đăng ký | Đang ở màn hình `RegisterActivity` | 1. Để trống ô Họ và tên.<br>2. Nhập đầy đủ các ô còn lại.<br>3. Bấm "ĐĂNG KÝ NGAY". | Báo lỗi "Họ tên không được để trống" tại `tilFullName`, focus vào `etFullName`, không gọi API. | **Pass** |
| **TC-MH03-02** | Nhập Email sai định dạng | Đang ở màn hình `RegisterActivity` | 1. Nhập Email sai định dạng (VD: `"nguyenvana@"` hoặc `"nguyenvana"`).<br>2. Nhập các ô khác hợp lệ.<br>3. Bấm "ĐĂNG KÝ NGAY". | Báo lỗi "Định dạng Email không hợp lệ" tại `tilEmail`, focus vào `etEmail`, không gửi request. | **Pass** |
| **TC-MH03-03** | Nhập Số điện thoại sai định dạng | Đang ở màn hình `RegisterActivity` | 1. Nhập SĐT sai định dạng (dưới 10 số, hoặc không bắt đầu bằng số 0, VD: `"1234567890"` hoặc `"0912345"`).<br>2. Bấm "ĐĂNG KÝ NGAY". | Báo lỗi "Số điện thoại không hợp lệ (cần 10 chữ số, bắt đầu bằng 0)" tại `tilPhone`, focus vào `etPhone`, không gọi API. | **Pass** |
| **TC-MH03-04** | Nhập Mật khẩu không đạt độ phức tạp | Đang ở màn hình `RegisterActivity` | 1. Nhập Mật khẩu dưới 6 ký tự (VD: `"12345"`) hoặc đủ 6 ký tự nhưng không có chữ số nào (VD: `"abcdef"`).<br>2. Bấm "ĐĂNG KÝ NGAY". | Báo lỗi "Mật khẩu phải có ít nhất 6 ký tự và chứa ít nhất 1 chữ số" tại `tilPassword`, focus vào `etPassword`. | **Pass** |
| **TC-MH03-05** | Nhập Xác nhận mật khẩu không trùng với Mật khẩu | Đang ở màn hình `RegisterActivity` | 1. Nhập Mật khẩu: `Matkhau123`.<br>2. Nhập Xác nhận mật khẩu: `Khac123`.<br>3. Bấm "ĐĂNG KÝ NGAY". | Báo lỗi "Mật khẩu xác nhận không khớp" tại `tilConfirmPassword`, focus vào `etConfirmPassword`. | **Pass** |
| **TC-MH03-06** | Đăng ký với Email đã tồn tại trong hệ thống (jackacevn@gmail.com) | Email `jackacevn@gmail.com` đã tồn tại trong CSDL | 1. Nhập Email: `jackacevn@gmail.com`.<br>2. Điền các trường khác hợp lệ.<br>3. Bấm "ĐĂNG KÝ NGAY". | API trả lỗi 400, app hiển thị thông báo "Email này đã được sử dụng bởi một tài khoản khác." ngay dưới ô Email (`tilEmail`) và qua Toast. | **Pass** |
| **TC-MH03-07** | Đăng ký với Email mới nhưng Số điện thoại đã tồn tại (0896699703) | SĐT `0896699703` đã tồn tại trong CSDL | 1. Nhập Email mới (VD: `user.moi@gmail.com`).<br>2. Nhập SĐT đã có: `0896699703`.<br>3. Bấm "ĐĂNG KÝ NGAY". | API trả lỗi 400, app hiển thị thông báo "Số điện thoại này đã được sử dụng bởi một tài khoản khác." ngay dưới ô Số điện thoại (`tilPhone`) và qua Toast. | **Pass** |
| **TC-MH03-08** | Đăng ký hợp lệ khi bỏ trống ô Địa chỉ (Tùy chọn) | Email và SĐT chưa tồn tại trong hệ thống | 1. Điền đầy đủ 5 trường bắt buộc (*).<br>2. Bỏ trống ô Địa chỉ nhận hàng.<br>3. Bấm "ĐĂNG KÝ NGAY". | Tạo tài khoản thành công, tự động lưu JWT Token vào `TokenManager` và chuyển thẳng vào `HomeActivity` (MH04). | **Pass** |
| **TC-MH03-09** | Đăng ký hợp lệ khi điền đầy đủ cả 6 ô (bao gồm Địa chỉ) | Email và SĐT hoàn toàn mới | 1. Điền đầy đủ cả 6 ô (kèm địa chỉ nhận hàng).<br>2. Bấm "ĐĂNG KÝ NGAY".<br>3. Tắt hẳn ứng dụng và mở lại từ Splash (MH01). | Tạo tài khoản thành công, tự động đăng nhập vào `HomeActivity`. Khi tắt app mở lại từ Splash (MH01), app nhận diện đúng tài khoản vừa đăng ký. | **Pass** |
| **TC-MH03-10** | Bấm dòng "Đã có tài khoản? Đăng nhập" (`tvLogin`) | Đang ở màn hình `RegisterActivity` | 1. Tại màn hình Đăng ký, bấm dòng "Đã có tài khoản? Đăng nhập" (`tvLogin`). | Quay trở lại màn hình `LoginActivity` (MH02) mượt mà, không tạo trùng lặp màn hình trên Back Stack (`CLEAR_TOP`). | **Pass** |

---

## 5. BẢNG TEST CASES CHI TIẾT CHO MÀN HÌNH MH10 — TÀI KHOẢN & HỒ SƠ CÁ NHÂN (`ProfileActivity`)

| Mã TC | Tên kịch bản | Điều kiện tiên quyết | Các bước thực hiện | Kết quả mong đợi | Kết quả thực tế (Pass/Fail) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-MH10-01** | Mở màn hình Tài khoản khi đã đăng nhập | Đã đăng nhập vào app, có phiên hợp lệ | 1. Mở màn hình `ProfileActivity`.<br>2. Quan sát dữ liệu hiển thị. | Tự động gọi `GET /api/Auth/me` và hiển thị đúng Họ tên, Email, Số điện thoại, Ngày sinh, Địa chỉ và Ảnh đại diện của tài khoản. | **Pass** |
| **TC-MH10-02** | Sửa hồ sơ cá nhân: Xóa trống Họ tên rồi bấm Lưu | Đang ở màn hình `ProfileActivity` | 1. Bấm nút "Sửa hồ sơ cá nhân".<br>2. Xóa trống ô Họ tên.<br>3. Bấm nút "Lưu thay đổi". | Báo lỗi *"Họ tên không được để trống"* tại ô Họ tên, không gửi API lên Server. | **Pass** |
| **TC-MH10-03** | Sửa hồ sơ cá nhân với thông tin hợp lệ | Đang mở dialog sửa hồ sơ | 1. Nhập Họ tên mới, Địa chỉ mới.<br>2. Chạm ô Ngày sinh chọn ngày hợp lệ (trong quá khứ).<br>3. Bấm "Lưu thay đổi". | Gọi `PUT /api/Auth/profile` thành công, giao diện MH10 và `TokenManager` cập nhật ngay thông tin mới. | **Pass** |
| **TC-MH10-04** | Đổi ảnh đại diện từ thư viện máy | Đang ở màn hình `ProfileActivity` | 1. Chạm vào ảnh đại diện tròn `ivAvatar` (hoặc nút trong dialog).<br>2. Chọn 1 ảnh từ thư viện. | App tự động resize & nén sang JPEG Base64 (< 5MB), gửi `PUT /api/Auth/profile` thành công, Server trả về `avatarUrl` mới (`/images/avatars/...jpg`) và `Glide` tải ảnh đại diện mới lên `ivAvatar`. | **Pass** |
| **TC-MH10-05** | Đổi mật khẩu: Nhập mật khẩu mới không hợp lệ | Đang mở dialog đổi mật khẩu | 1. Nhập Mật khẩu mới trùng Mật khẩu hiện tại (hoặc mật khẩu < 6 ký tự / không có số / xác nhận không khớp).<br>2. Bấm "Đổi mật khẩu". | Báo lỗi validation tương ứng ngay trên Dialog (VD: *"Mật khẩu mới không được trùng với mật khẩu hiện tại"*), không gửi API. | **Pass** |
| **TC-MH10-06** | Đổi mật khẩu: Nhập sai Mật khẩu hiện tại | Backend đang chạy | 1. Nhập Mật khẩu hiện tại sai.<br>2. Nhập Mật khẩu mới hợp lệ.<br>3. Bấm "Đổi mật khẩu". | API trả lỗi 400 *"Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu hiện tại."*, hiển thị thông báo lỗi qua Toast. | **Pass** |
| **TC-MH10-07** | Đổi mật khẩu thành công | Backend đang chạy, nhập đúng mật khẩu hiện tại | 1. Nhập Mật khẩu hiện tại: `12345`.<br>2. Mật khẩu mới: `123456`.<br>3. Xác nhận: `123456`.<br>4. Bấm "Đổi mật khẩu". | Hiện thông báo yêu cầu đăng nhập lại, tự động xóa sạch Token cũ và đẩy về `LoginActivity` (MH02). Bấm Back không quay lại được MH10. | **Pass** |
| **TC-MH10-08** | Kiểm tra các nút điều hướng từ MH10 | Đang ở màn hình `ProfileActivity` | 1. Bấm lần lượt: "Sổ địa chỉ giao hàng", "Lịch sử đơn hàng", "Yêu cầu đổi trả của tôi". | Mở đúng màn hình: `AddressListActivity` (MH11), `OrderHistoryActivity` (MH12), `ReturnOrderActivity` (MH13). | **Pass** |
| **TC-MH10-09** | Đăng xuất an toàn với Server | Đang đăng nhập, có kết nối mạng | 1. Bấm nút "Đăng xuất".<br>2. Hiện hộp thoại xác nhận -> Chọn "Hủy" thì ở lại.<br>3. Bấm lại chọn "Đăng xuất". | Gọi `POST /api/Auth/revoke-token` thu hồi Refresh Token trong DB, xóa sạch `TokenManager` và chuyển về `LoginActivity` (`CLEAR_TASK`). | **Pass** |
| **TC-MH10-10** | Đăng xuất khi mất kết nối mạng (Offline) | Bật chế độ máy bay hoặc tắt mạng | 1. Tắt kết nối Internet.<br>2. Bấm "Đăng xuất" -> Xác nhận đăng xuất. | App vẫn xử lý nhánh `onError` an toàn, xóa sạch `TokenManager` cục bộ và đưa người dùng về `LoginActivity`, không bị kẹt trong ứng dụng. | **Pass** |

---

## 6. BẢNG TEST CASES CHI TIẾT CHO MÀN HÌNH MH13 — YÊU CẦU TRẢ HÀNG & LỊCH SỬ ĐỔI TRẢ (`ReturnOrderActivity`)

| Mã TC | Tên kịch bản | Điều kiện tiên quyết | Các bước thực hiện | Kết quả mong đợi | Kết quả thực tế (Pass/Fail) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-MH13-01** | Mở MH13 từ nút "Yêu cầu đổi trả của tôi" tại MH10 | Đã đăng nhập, mở từ `ProfileActivity` (`orderId <= 0`) | 1. Tại `ProfileActivity`, bấm "Yêu cầu đổi trả của tôi".<br>2. Quan sát giao diện MH13. | Form tạo yêu cầu tự động ẩn (`layoutCreateReturnForm.visibility = GONE`), tiêu đề Toolbar là "Yêu cầu đổi trả của tôi", tự động tải danh sách lịch sử trả hàng (`GET /api/ReturnRequests/my-returns`). | **Pass** |
| **TC-MH13-02** | Mở MH13 từ đơn hàng Completed tại MH12 | Đơn hàng trạng thái `Completed` (status = 3) tại MH12 | 1. Bấm "Trả hàng" tại đơn `#ORD-...`.<br>2. Chuyển sang MH13 với `EXTRA_ORDER_ID`. | Form tạo mới hiển thị đầy đủ, gọi `GET /api/orders/{id}`, hiện mã đơn, ngày đặt, danh sách sách đã mua và số tiền hoàn dự kiến ban đầu. | **Pass** |
| **TC-MH13-03** | Thao tác chọn sản phẩm và tăng giảm số lượng trả | Đang mở form tạo yêu cầu trả hàng | 1. Tick/bỏ tick CheckBox chọn sản phẩm.<br>2. Bấm nút `+` / `-` điều chỉnh số lượng. | Số lượng trả không nhỏ hơn 1 và không vượt quá số lượng đã mua. Số tiền hoàn dự kiến tự động tính lại ngay lập tức theo thời gian thực: $\sum (\text{giá} \times \text{số lượng trả})$. | **Pass** |
| **TC-MH13-04** | Bỏ tick toàn bộ sản phẩm rồi bấm Gửi yêu cầu | Đang ở form tạo yêu cầu trả hàng | 1. Bỏ chọn tất cả CheckBox của các sản phẩm.<br>2. Bấm "Gửi yêu cầu trả hàng". | Báo Toast lỗi: *"Vui lòng chọn ít nhất 1 sản phẩm muốn trả"*, dừng lại không gọi API. | **Pass** |
| **TC-MH13-05** | Bỏ trống Lý do trả hàng hoặc Thông tin ngân hàng | Có chọn sản phẩm hợp lệ | 1. Để trống ô Lý do hoặc một trong các ô Tên NH / Số TK / Chủ TK.<br>2. Bấm "Gửi yêu cầu trả hàng". | Báo lỗi validation đỏ tại ô tương ứng (`tilReason` hoặc cụm ngân hàng), không gọi API. | **Pass** |
| **TC-MH13-06** | Chọn ảnh minh chứng từ thư viện (tối đa 5 ảnh) | Đang ở form tạo yêu cầu trả hàng | 1. Bấm "Chọn ảnh minh chứng".<br>2. Chọn từ 1 đến 5 ảnh từ máy ảo. | App tự động resize & nén sang JPEG Base64 (< 5MB), hiển thị *"Đã chọn X/5 ảnh minh chứng"*, hiện nút "Xóa ảnh đã chọn" để chọn lại nếu muốn. | **Pass** |
| **TC-MH13-07** | Gửi yêu cầu trả hàng hợp lệ thành công | Đơn hàng `Completed`, điền đầy đủ thông tin hợp lệ | 1. Chọn sản phẩm, nhập lý do, thông tin ngân hàng, chọn ảnh.<br>2. Bấm "Gửi yêu cầu trả hàng". | Gọi `POST /api/ReturnRequests` thành công, hiện Toast *"Gửi yêu cầu trả hàng thành công!"*, form tạo mới tự động ẩn đi và phiếu trả hàng mới xuất hiện ngay ở đầu danh sách Lịch sử bên dưới với trạng thái *"Chờ duyệt"*. | **Pass** |
| **TC-MH13-08** | Cố tình gửi lại yêu cầu trả hàng cho đơn đã gửi trước đó | Đơn hàng đã có phiếu trả hàng trong CSDL | 1. Mở lại MH13 với orderId của đơn đã gửi yêu cầu.<br>2. Điền thông tin và bấm gửi. | Backend trả lỗi 400 *"Đơn hàng này đã có yêu cầu đổi trả được ghi nhận trước đó."*, app hiển thị Toast cảnh báo chi tiết, không tạo trùng lặp. | **Pass** |
| **TC-MH13-09** | Mở MH13 với đơn hàng chưa hoàn thành (Pending / Shipping) | Đơn hàng có `status != 3` | 1. Truyền orderId của đơn chưa hoàn thành sang MH13. | Nút "Gửi yêu cầu trả hàng" bị khóa disable (`enabled = false`), app hiển thị Toast cảnh báo chỉ đơn hàng Đã hoàn thành mới được đổi trả. | **Pass** |
| **TC-MH13-10** | Kiểm tra hiển thị danh sách Lịch sử đổi trả của tôi | Tài khoản đã từng gửi yêu cầu đổi trả | 1. Quan sát danh sách lịch sử tại MH13. | Hiển thị đầy đủ: Mã đơn hàng & Mã phiếu (#ORD... - Phiếu #..), Badge trạng thái (`Chờ duyệt`, `Đã duyệt`, `Đã hoàn tiền`, `Từ chối`), Số tiền hoàn định dạng VNĐ, Lý do trả, Thông tin tài khoản nhận tiền và Ngày gửi. | **Pass** |

---

## 7. KẾT LUẬN VÀ ĐÁNH GIÁ CHẤT LƯỢNG TOÀN DIỆN

1. **Về tính toàn vẹn chức năng:** Toàn bộ **43 Test Cases chi tiết** trải rộng trên toàn bộ phân hệ của Trần Duy Long:
   - **MH02 (Đăng nhập):** 8 Test Cases
   - **MH01 (Khởi động):** 5 Test Cases
   - **MH03 (Đăng ký tài khoản):** 10 Test Cases
   - **MH10 (Tài khoản & Hồ sơ):** 10 Test Cases
   - **MH13 (Yêu cầu trả hàng & Lịch sử đổi trả):** 10 Test Cases
   - **Tất cả 43 Test Cases đều đạt kết quả PASS 100%.**

2. **Về tính an toàn & bảo mật:**
   - Bộ nhận diện token 2 lớp JWT (Access Token 30 phút, Refresh Token 14 ngày) với Silent Refresh tự động.
   - Cơ chế Fail-safe Logout đảm bảo thu hồi token trên Server đồng thời dọn sạch bộ nhớ máy khách ngay cả khi mất kết nối mạng.
   - Nén ảnh chuẩn JPEG Base64 với chữ ký magic bytes `FF D8 FF` bảo vệ API chống giả mạo file độc hại.
   - Ràng buộc nghiệp vụ đổi trả hàng chặt chẽ: chỉ đơn hàng `Completed` mới được đổi trả, giới hạn số lượng trả $\le$ số lượng đã mua, kiểm soát trạng thái không cho gửi trùng lặp.

3. **Về giao diện & Trải nghiệm người dùng (UI/UX):**
   - Thiết kế đồng bộ ngôn ngữ thương hiệu Fahasa Red hiện đại, bo góc thẻ nổi thanh lịch.
   - Tính toán tiền hoàn thời gian thực, giao diện linh hoạt chuyển đổi giữa chế độ tạo mới và chế độ xem lịch sử.
