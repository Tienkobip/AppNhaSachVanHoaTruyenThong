package com.webbansach.app.ui.auth_long.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.webbansach.app.R;
import com.webbansach.app.core.utils.CurrencyUtils;
import com.webbansach.app.databinding.ItemLongReturnProductBinding;
import com.webbansach.app.models.OrderItemDto;
import com.webbansach.app.models.ReturnItemInputDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter danh sách sản phẩm trong đơn hàng phục vụ người dùng chọn trả hàng (MH13).
 */
public class ReturnProductSelectAdapter extends RecyclerView.Adapter<ReturnProductSelectAdapter.ViewHolder> {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(double estimatedRefundAmount);
    }

    public static class ReturnItemState {
        public final OrderItemDto item;
        public boolean selected;
        public int returnQuantity;

        public ReturnItemState(OrderItemDto item) {
            this.item = item;
            this.selected = true;
            this.returnQuantity = Math.max(1, item.quantity);
        }
    }

    private final List<ReturnItemState> items = new ArrayList<>();
    private OnSelectionChangedListener listener;

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void setData(List<OrderItemDto> orderItems) {
        items.clear();
        if (orderItems != null) {
            for (OrderItemDto orderItem : orderItems) {
                items.add(new ReturnItemState(orderItem));
            }
        }
        notifyDataSetChanged();
        notifyEstimatedRefundChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLongReturnProductBinding binding = ItemLongReturnProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReturnItemState state = items.get(position);
        OrderItemDto item = state.item;

        holder.binding.tvProductTitle.setText(item.title != null ? item.title : "");

        double price = item.finalPrice > 0 ? item.finalPrice : item.unitPrice;
        String priceText = CurrencyUtils.formatVnd(price) + " • Đã mua: " + item.quantity;
        holder.binding.tvUnitPriceAndMaxQty.setText(priceText);

        holder.binding.cbSelectProduct.setChecked(state.selected);
        holder.binding.tvReturnQty.setText(String.valueOf(state.returnQuantity));

        // Load ảnh sản phẩm
        String fullImageUrl = CurrencyUtils.resolveImageUrl(item.imageUrl);
        Glide.with(holder.itemView.getContext())
                .load(fullImageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.binding.ivProductImage);

        // Sự kiện tick CheckBox
        holder.binding.cbSelectProduct.setOnCheckedChangeListener((buttonView, isChecked) -> {
            state.selected = isChecked;
            notifyEstimatedRefundChanged();
        });

        // Nút giảm số lượng trả
        holder.binding.btnDecreaseQty.setOnClickListener(v -> {
            if (state.returnQuantity > 1) {
                state.returnQuantity--;
                holder.binding.tvReturnQty.setText(String.valueOf(state.returnQuantity));
                notifyEstimatedRefundChanged();
            }
        });

        // Nút tăng số lượng trả (không vượt quá số lượng đã mua)
        holder.binding.btnIncreaseQty.setOnClickListener(v -> {
            if (state.returnQuantity < item.quantity) {
                state.returnQuantity++;
                holder.binding.tvReturnQty.setText(String.valueOf(state.returnQuantity));
                notifyEstimatedRefundChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Tính toán số tiền hoàn dự kiến dựa trên các sản phẩm được tick chọn
     */
    public double getEstimatedRefundAmount() {
        double total = 0.0;
        for (ReturnItemState state : items) {
            if (state.selected) {
                double price = state.item.finalPrice > 0 ? state.item.finalPrice : state.item.unitPrice;
                total += price * state.returnQuantity;
            }
        }
        return total;
    }

    private void notifyEstimatedRefundChanged() {
        if (listener != null) {
            listener.onSelectionChanged(getEstimatedRefundAmount());
        }
    }

    /**
     * Lấy danh sách các sản phẩm đang được tick chọn để đóng gói vào CreateReturnRequestDto
     */
    public List<ReturnItemInputDto> getSelectedReturnItems() {
        List<ReturnItemInputDto> selected = new ArrayList<>();
        for (ReturnItemState state : items) {
            if (state.selected && state.returnQuantity > 0) {
                selected.add(new ReturnItemInputDto(state.item.productId, state.returnQuantity, null));
            }
        }
        return selected;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemLongReturnProductBinding binding;

        ViewHolder(ItemLongReturnProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
