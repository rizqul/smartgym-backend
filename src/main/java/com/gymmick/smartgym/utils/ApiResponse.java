package com.gymmick.smartgym.utils;

import java.util.List;

import org.springframework.http.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gymmick.smartgym.dto.pagination.PaginationMeta;

import lombok.*;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Integer statusCode;
    private String message;
    private T data;
    private PaginationMeta paginate;

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message) {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .statusCode(status.value())
                .message(message)
                .build();

        return ResponseEntity.status(status.value()).body(apiResponse);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, T data) {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .statusCode(status.value())
                .message("OK")
                .data(data)
                .build();

        return ResponseEntity.status(status.value()).body(apiResponse);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, T data, PaginationMeta paginate) {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .statusCode(status.value())
                .message("OK")
                .data(data)
                .paginate(paginate)
                .build();

        return ResponseEntity.status(status.value()).body(apiResponse);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message, T data) {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();

        return ResponseEntity.status(status.value()).body(apiResponse);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message, T data,
            List<String> cookies) {
        HttpHeaders cookieHeaders = new HttpHeaders();
        for (String cookie : cookies) {
            cookieHeaders.add("set-cookie", cookie);
        }

        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .statusCode(status.value())
                .message(message)
                .data(data)
                .build();

        return ResponseEntity.status(status.value()).headers(cookieHeaders).body(apiResponse);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String message, List<String> cookies) {
        HttpHeaders cookieHeaders = new HttpHeaders();
        for (String cookie : cookies) {
            cookieHeaders.add("set-cookie", cookie);
        }

        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .statusCode(status.value())
                .message(message)
                .build();

        return ResponseEntity.status(status.value()).headers(cookieHeaders).body(apiResponse);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, List<String> cookies) {
        HttpHeaders cookieHeaders = new HttpHeaders();

        for (String cookie : cookies) {
            cookieHeaders.add("set-cookie", cookie);
        }

        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .build();

        return ResponseEntity.status(status.value()).headers(cookieHeaders).body(apiResponse);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success() {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT.value()).body(apiResponse);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .statusCode(status.value())
                .message(message)
                .build();

        return ResponseEntity.status(status.value()).body(apiResponse);
    }
}
