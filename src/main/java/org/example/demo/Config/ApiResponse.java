package org.example.demo.Config;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        Boolean success,
        String message,
        T data,
        LocalDateTime timestamp,
        String path
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                true,
                message,
                data,
                LocalDateTime.now(),
                null
        );
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(
                false,
                message,
                null,
                LocalDateTime.now(),
                null
        );
    }
}