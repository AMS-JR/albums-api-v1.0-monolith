package com.afrosofttech.rest_jwt_demo.util;


import com.afrosofttech.rest_jwt_demo.dto.common.ApiError;
import com.afrosofttech.rest_jwt_demo.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseBuilder {

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data, null);
        return new ResponseEntity<>(response, status);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseBuilder.success(message, data, HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseBuilder.success(message, data, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, String code, List<String> details, HttpStatus status) {
        ApiError error = new ApiError(code, details);
        ApiResponse<T> response = new ApiResponse<>(false, message, null, error);
        return new ResponseEntity<>(response, status);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, String code, List<String> details) {
        return ResponseBuilder.error(message, code, details, HttpStatus.BAD_REQUEST);
    }
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message, String code, List<String> details) {
        return ResponseBuilder.error(message, code, details, HttpStatus.FORBIDDEN);
    }

    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message, String code, List<String> details) {
        return ResponseBuilder.error(message, code, details, HttpStatus.UNAUTHORIZED);
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message, String code, List<String> details) {
        return ResponseBuilder.error(message, code, details, HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseEntity<ApiResponse<T>> internalError(String message, String code, List<String> details) {
        return ResponseBuilder.error(message, code, details, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public static <T> ResponseEntity<ApiResponse<T>> unsupportedMediaType(String message, String code, List<String> details) {
        return ResponseBuilder.error(message, code, details, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
