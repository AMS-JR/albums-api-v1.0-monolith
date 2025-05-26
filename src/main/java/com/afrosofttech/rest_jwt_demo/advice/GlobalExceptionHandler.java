package com.afrosofttech.rest_jwt_demo.advice;

import com.afrosofttech.rest_jwt_demo.exception.BadRequestException;
import com.afrosofttech.rest_jwt_demo.exception.ResourceNotFoundException;
import com.afrosofttech.rest_jwt_demo.exception.UnauthorizedAccessException;
import com.afrosofttech.rest_jwt_demo.exception.UnsupportedMediaTypException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(UnsupportedMediaTypException.class)
    public ResponseEntity<String> handleUnsupportedMediaType(UnsupportedMediaTypException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + ex.getMessage());
    }

//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
//        return ResponseBuilder.error("Resource not found", "NOT_FOUND", List.of(ex.getMessage()), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
//        return ResponseBuilder.error("Internal server error", "INTERNAL_ERROR", List.of(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
