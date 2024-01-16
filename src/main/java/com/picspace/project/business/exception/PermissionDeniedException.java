package com.picspace.project.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PermissionDeniedException extends ResponseStatusException {
    public PermissionDeniedException(){
        super(HttpStatus.FORBIDDEN, "Action not allowed!");
    }
}
