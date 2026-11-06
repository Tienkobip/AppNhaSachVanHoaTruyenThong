package com.webbansach.app.models;

import java.util.List;

public class ApiResponse<T> {
    public boolean success;
    public String message;
    public T data;
    public List<String> errors;
}
