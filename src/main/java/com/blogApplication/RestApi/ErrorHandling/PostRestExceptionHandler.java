package com.blogApplication.RestApi.ErrorHandling;

import com.blogApplication.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PostRestExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(PostNotFoundException exception){
        ErrorResponse errorResponse =new ErrorResponse(HttpStatus.NOT_FOUND.value(),exception.getMessage(),System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception exception){
        ErrorResponse errorResponse =new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
}
