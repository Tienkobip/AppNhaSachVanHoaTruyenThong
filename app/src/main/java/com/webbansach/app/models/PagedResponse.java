package com.webbansach.app.models;

import java.util.List;

public class PagedResponse<T> {
    public List<T> items;
    public int totalCount;
    public int page;
    public int pageSize;
    public int totalPages;
}
