package com.blogApplication.RestApi.ErrorHandling;

public class PostNotFoundException extends RuntimeException{
    public PostNotFoundException(String message) {
        super(message);
    }
}
