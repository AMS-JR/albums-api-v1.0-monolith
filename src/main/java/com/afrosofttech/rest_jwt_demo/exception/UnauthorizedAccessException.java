package com.afrosofttech.rest_jwt_demo.exception;

public class UnauthorizedAccessException extends RuntimeException{
    public UnauthorizedAccessException(String message){
        super(message);
    }
}
