package com.feauture.app.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record ApiResponse<T>(boolean success, String msg, T data) {

    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(true, msg, data);
    }

    public static ResponseStatusException error(HttpStatus status, String msg) {
        return new ResponseStatusException(status, msg);
    }
}