package com.blogApplication.RestApi.ErrorHandling;

public class SecurityException extends RuntimeException{
    public SecurityException(String message) {
        super(message);
    }
}
