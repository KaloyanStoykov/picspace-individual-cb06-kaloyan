package com.picspace.project.configuration.exceptionHandle;

import com.picspace.project.business.exception.UserNotFoundException;
import com.picspace.project.business.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception){
        ExceptionErrorResponse exceptionErrorResponse = ExceptionErrorResponse.builder().status(HttpStatus.NOT_FOUND.name()).message(exception.getMessage()).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionErrorResponse);
    }

    @ExceptionHandler({UsernameAlreadyExistsException.class})
    public ResponseEntity<Object>handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exception){
        ExceptionErrorResponse exceptionErrorResponse = ExceptionErrorResponse.builder().status(HttpStatus.CONFLICT.name()).message(exception.getMessage()).build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionErrorResponse);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
        ExceptionErrorResponse exceptionErrorResponse = ExceptionErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.name())
                .message("Invalid username and/or password!")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionErrorResponse);
    }

}
