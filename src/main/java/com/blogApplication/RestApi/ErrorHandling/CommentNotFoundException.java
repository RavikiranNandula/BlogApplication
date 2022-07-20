package com.blogApplication.RestApi.ErrorHandling;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(String message) {
        super(message);
    }
}
