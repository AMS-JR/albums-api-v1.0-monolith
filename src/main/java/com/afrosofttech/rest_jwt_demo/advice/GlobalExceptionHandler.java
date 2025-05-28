package com.afrosofttech.rest_jwt_demo.advice;

import com.afrosofttech.rest_jwt_demo.dto.common.ApiResponse;
import com.afrosofttech.rest_jwt_demo.exception.BadRequestException;
import com.afrosofttech.rest_jwt_demo.exception.ResourceNotFoundException;
import com.afrosofttech.rest_jwt_demo.exception.UnauthorizedAccessException;
import com.afrosofttech.rest_jwt_demo.exception.UnsupportedMediaTypException;
import com.afrosofttech.rest_jwt_demo.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        return ResponseBuilder.notFound(ex.getMessage(), HttpStatus.NOT_FOUND.name(), List.of(ex.getMessage()));
    }
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        return ResponseBuilder.forbidden(ex.getMessage(),
                HttpStatus.FORBIDDEN.name(),
                List.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase()));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseBuilder.error(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.name(),
                List.of(ex.getMessage()));
    }
    @ExceptionHandler(UnsupportedMediaTypException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(UnsupportedMediaTypException ex) {
        return ResponseBuilder.unsupportedMediaType(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.name(),
                List.of(ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
            return ResponseBuilder.internalError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    List.of("An unexpected error occurred: " + ex.getMessage()));
    }

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
//        return ResponseBuilder.error("Resource not found", "NOT_FOUND", List.of(ex.getMessage()), HttpStatus.NOT_FOUND);
//    }
//
}
