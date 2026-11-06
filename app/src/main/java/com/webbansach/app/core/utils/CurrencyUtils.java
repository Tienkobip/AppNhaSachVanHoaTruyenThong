package com.webbansach.app.core.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Tiện ích định dạng tiền tệ VNĐ và xử lý đường dẫn ảnh.
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public final class CurrencyUtils {

    private static final DecimalFormat VND_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        VND_FORMAT = new DecimalFormat("#,###", symbols);
    }

    private CurrencyUtils() {}

    /**
     * Định dạng số tiền sang chuỗi VNĐ. Ví dụ: 185000 -> "185.000 đ"
     * @param amount Số tiền (double)
     * @return Chuỗi tiền tệ đã format
     */
    public static String formatVnd(double amount) {
        return VND_FORMAT.format(amount) + " đ";
    }

    /**
     * Nối đường dẫn ảnh đầy đủ từ đường dẫn tương đối trả về từ API.
     * Nếu url bắt đầu bằng "/" thì ghép với BASE_URL (bỏ dấu / cuối).
     * Nếu url đã là đường dẫn tuyệt đối (http/https) thì giữ nguyên.
     *
     * @param relativeUrl Đường dẫn ảnh từ API (VD: "/images/covers/truyen-kieu.svg")
     * @return Đường dẫn ảnh đầy đủ
     */
    public static String resolveImageUrl(String relativeUrl) {
        if (relativeUrl == null || relativeUrl.isEmpty()) {
            return "";
        }
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        // BASE_URL đã có dấu "/" cuối, relativeUrl bắt đầu bằng "/" -> bỏ 1 dấu /
        String baseUrl = Constants.BASE_URL;
        if (baseUrl.endsWith("/") && relativeUrl.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + relativeUrl;
        }
        return baseUrl + relativeUrl;
    }
}
