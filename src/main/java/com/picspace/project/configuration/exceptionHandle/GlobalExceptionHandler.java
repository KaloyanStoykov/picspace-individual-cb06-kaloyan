package com.picspace.project.configuration.exceptionHandle;

import com.picspace.project.business.exception.*;
import jakarta.persistence.ElementCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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

    @ExceptionHandler({NoFilteredUsersFoundException.class})
    public ResponseEntity<Object> handleFilterUsersNotFound(NoFilteredUsersFoundException exception) {
        ExceptionErrorResponse exceptionErrorResponse = ExceptionErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .message(exception.getReason())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionErrorResponse);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception){
        ExceptionErrorResponse exceptionErrorResponse = ExceptionErrorResponse.builder().status(HttpStatus.BAD_REQUEST.name()).message("Invalid data supplied!").build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionErrorResponse);
    }

    @ExceptionHandler({EntryNotFoundException.class})
    public ResponseEntity<Object> handleEntryNotFound(EntryNotFoundException exception){
        ExceptionErrorResponse exceptionErrorResponse = ExceptionErrorResponse.builder().status(HttpStatus.NOT_FOUND.name()).message(exception.getMessage()).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionErrorResponse);
    }

    @ExceptionHandler({PermissionDeniedException.class})
    public ResponseEntity<Object> handlePermissionDenied(PermissionDeniedException exception){
        ExceptionErrorResponse exceptionErrorResponse = ExceptionErrorResponse.builder().status(HttpStatus.FORBIDDEN.name()).message(exception.getMessage()).build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionErrorResponse);
    }


}
