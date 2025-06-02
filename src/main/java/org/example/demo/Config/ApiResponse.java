package org.example.demo.Config;

public record ApiResponse<T>(String message, T data, boolean success) {

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(message, data, true);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, null, false);
    }
}
