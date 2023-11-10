package com.picspace.project.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


public class InvalidParametersSuppliedException extends ResponseStatusException {

    public InvalidParametersSuppliedException(String errorCode){
        super(HttpStatus.BAD_REQUEST, errorCode);
    }
}
