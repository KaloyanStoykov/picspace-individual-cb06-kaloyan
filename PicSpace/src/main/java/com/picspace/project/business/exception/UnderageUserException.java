package com.picspace.project.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnderageUserException extends RuntimeException {
    public UnderageUserException(){
        super("User is underage (under 18 years old)");
    }
}