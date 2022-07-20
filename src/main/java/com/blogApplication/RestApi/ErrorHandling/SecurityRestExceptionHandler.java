package com.blogApplication.RestApi.ErrorHandling;

import com.blogApplication.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SecurityRestExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> nonLoggedInException(SecurityException exception){
        ErrorResponse errorResponse=new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity< >(errorResponse,HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> authenticationException(Exception exception){
        ErrorResponse errorResponse=new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity< >(errorResponse,HttpStatus.UNAUTHORIZED);
    }
}
