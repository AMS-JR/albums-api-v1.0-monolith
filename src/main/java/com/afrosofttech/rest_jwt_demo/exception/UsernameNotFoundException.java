package com.afrosofttech.rest_jwt_demo.exception;

public class UsernameNotFoundException extends RuntimeException{
    public UsernameNotFoundException(String message) {
        super(message);
    }
}
