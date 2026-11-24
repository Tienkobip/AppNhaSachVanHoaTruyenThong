package com.webbansach.app.ui.auth_long.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.webbansach.app.core.utils.CurrencyUtils;
import com.webbansach.app.databinding.ItemLongReturnHistoryBinding;
import com.webbansach.app.models.ReturnRequestSummaryDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter danh sách lịch sử yêu cầu trả hàng của người dùng (MH13).
 */
public class ReturnHistoryAdapter extends RecyclerView.Adapter<ReturnHistoryAdapter.ViewHolder> {

    private final List<ReturnRequestSummaryDto> items = new ArrayList<>();

    public void submitList(List<ReturnRequestSummaryDto> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLongReturnHistoryBinding binding = ItemLongReturnHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReturnRequestSummaryDto item = items.get(position);

        String orderText = "Đơn hàng: #" + (item.orderCode != null ? item.orderCode : item.orderId)
                + " (Phiếu #" + item.returnRequestId + ")";
        holder.binding.tvReturnOrderCode.setText(orderText);

        holder.binding.tvReturnStatus.setText(item.statusText != null ? item.statusText : "Đang xử lý");

        String refundText = "Hoàn tiền: " + CurrencyUtils.formatVnd(item.refundAmount);
        holder.binding.tvReturnRefundAmount.setText(refundText);

        holder.binding.tvReturnReason.setText("Lý do: " + (item.reason != null ? item.reason : "Không có"));

        if (item.bankAccountInfo != null && !item.bankAccountInfo.trim().isEmpty()) {
            holder.binding.tvReturnBankInfo.setText("Tài khoản hoàn: " + item.bankAccountInfo.trim());
            holder.binding.tvReturnBankInfo.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvReturnBankInfo.setVisibility(View.GONE);
        }

        // Format requestedAt (lấy yyyy-MM-dd HH:mm nếu là ISO)
        String dateText = item.requestedAt != null ? item.requestedAt : "";
        if (dateText.contains("T")) {
            dateText = dateText.replace("T", " ");
            if (dateText.length() >= 16) {
                dateText = dateText.substring(0, 16);
            }
        }
        holder.binding.tvReturnDate.setText("Ngày gửi: " + dateText);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemLongReturnHistoryBinding binding;

        ViewHolder(ItemLongReturnHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
