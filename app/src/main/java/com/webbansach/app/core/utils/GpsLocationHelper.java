package com.webbansach.app.core.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Tiện ích lấy vị trí GPS hiện tại và chuyển đổi sang địa chỉ Tiếng Việt.
 * Sử dụng FusedLocationProviderClient + Geocoder.
 * [KHÓA CHẶT] - Không được sửa file này khi code giao diện.
 */
public class GpsLocationHelper {

    /**
     * Callback interface trả về kết quả địa chỉ
     */
    public interface OnAddressListener {
        /** Gọi khi lấy địa chỉ thành công */
        void onAddressFound(String fullAddress);
        /** Gọi khi có lỗi */
        void onError(String errorMessage);
    }

    private final Context context;
    private final FusedLocationProviderClient fusedClient;

    public GpsLocationHelper(Context context) {
        this.context = context;
        this.fusedClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * Kiểm tra quyền GPS đã được cấp chưa.
     */
    public boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Lấy vị trí GPS hiện tại và chuyển đổi sang địa chỉ Tiếng Việt qua Geocoder.
     * Yêu cầu: Đã cấp quyền ACCESS_FINE_LOCATION hoặc ACCESS_COARSE_LOCATION.
     *
     * @param listener Callback nhận kết quả
     */
    @SuppressLint("MissingPermission")
    public void getCurrentAddress(@NonNull OnAddressListener listener) {
        if (!hasLocationPermission()) {
            listener.onError("Chưa cấp quyền truy cập vị trí. Vui lòng bật quyền GPS trong Cài đặt.");
            return;
        }

        // Lấy vị trí cuối cùng trước (nhanh)
        fusedClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                geocodeLocation(location.getLatitude(), location.getLongitude(), listener);
            } else {
                // Không có last location -> request fresh location
                requestFreshLocation(listener);
            }
        }).addOnFailureListener(e -> {
            listener.onError("Lỗi khi lấy vị trí: " + e.getMessage());
        });
    }

    @SuppressLint("MissingPermission")
    private void requestFreshLocation(@NonNull OnAddressListener listener) {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMaxUpdates(1)
                .build();

        fusedClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                fusedClient.removeLocationUpdates(this);
                if (locationResult.getLastLocation() != null) {
                    geocodeLocation(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude(),
                            listener
                    );
                } else {
                    listener.onError("Không thể xác định vị trí hiện tại. Vui lòng thử lại.");
                }
            }
        }, Looper.getMainLooper());
    }

    /**
     * Chuyển đổi tọa độ GPS sang chuỗi địa chỉ Tiếng Việt bằng Geocoder.
     */
    private void geocodeLocation(double latitude, double longitude, @NonNull OnAddressListener listener) {
        Geocoder geocoder = new Geocoder(context, new Locale("vi", "VN"));
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                // Ghép các dòng địa chỉ
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(address.getAddressLine(i));
                }
                String fullAddress = sb.toString();
                if (fullAddress.isEmpty()) {
                    fullAddress = address.getAdminArea() + ", " + address.getCountryName();
                }
                listener.onAddressFound(fullAddress);
            } else {
                listener.onError("Không tìm thấy địa chỉ cho vị trí hiện tại.");
            }
        } catch (IOException e) {
            listener.onError("Lỗi Geocoder: " + e.getMessage());
        }
    }
}
