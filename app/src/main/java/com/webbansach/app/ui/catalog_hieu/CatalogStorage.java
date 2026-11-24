package com.webbansach.app.ui.catalog_hieu;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webbansach.app.core.local.TokenManager;
import com.webbansach.app.models.ProductDto;
import com.webbansach.app.models.ProductListItemDto;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/** Local browsing preferences and review drafts owned by the catalog screens. */
final class CatalogStorage {
    private final SharedPreferences preferences;
    private final Gson gson = new Gson();

    CatalogStorage(Context context) {
        String userId = TokenManager.getInstance(context).getUserId();
        preferences = context.getSharedPreferences("hieu_catalog_" + userId, Context.MODE_PRIVATE);
    }

    private <T> List<T> readList(String key, Type type) {
        try {
            List<T> values = gson.fromJson(preferences.getString(key, "[]"), type);
            return values == null ? new ArrayList<>() : new ArrayList<>(values);
        } catch (RuntimeException invalidData) {
            return new ArrayList<>();
        }
    }

    List<String> searches() {
        List<String> values = readList("searches", new TypeToken<List<String>>() {}.getType());
        values.removeIf(value -> value == null || value.trim().isEmpty());
        return values;
    }

    void rememberSearch(String keyword) {
        if (keyword.isEmpty()) return;
        List<String> values = searches();
        values.removeIf(value -> value.equalsIgnoreCase(keyword));
        values.add(0, keyword);
        preferences.edit().putString("searches",
                gson.toJson(values.subList(0, Math.min(8, values.size())))).apply();
    }

    void clearSearches() {
        preferences.edit().remove("searches").apply();
    }

    List<ProductListItemDto> recentBooks() {
        List<ProductListItemDto> books = readList("recentBooks",
                new TypeToken<List<ProductListItemDto>>() {}.getType());
        books.removeIf(book -> book == null || book.productId <= 0);
        return books;
    }

    void rememberBook(ProductDto product) {
        ProductListItemDto book = new ProductListItemDto();
        book.productId = product.productId;
        book.title = product.title;
        book.authorName = product.authorName;
        book.mainImageUrl = product.mainImageUrl;
        book.price = product.price;
        book.finalPrice = product.finalPrice;
        book.discountPercent = product.discountPercent;
        book.averageRating = product.averageRating;
        List<ProductListItemDto> books = recentBooks();
        books.removeIf(item -> item.productId == book.productId);
        books.add(0, book);
        preferences.edit().putString("recentBooks",
                gson.toJson(books.subList(0, Math.min(10, books.size())))).apply();
    }

    String draftKey(int orderId, int productId) {
        return "draft_" + orderId + "_" + productId;
    }

    void saveDraft(int orderId, int productId, int rating, String comment) {
        String key = draftKey(orderId, productId);
        preferences.edit().putInt(key + "_rating", rating)
                .putString(key + "_comment", comment).apply();
    }

    int draftRating(int orderId, int productId) {
        return preferences.getInt(draftKey(orderId, productId) + "_rating", 0);
    }

    String draftComment(int orderId, int productId) {
        return preferences.getString(draftKey(orderId, productId) + "_comment", "");
    }

    void clearDraft(int orderId, int productId) {
        String key = draftKey(orderId, productId);
        preferences.edit().remove(key + "_rating").remove(key + "_comment").apply();
    }
}
