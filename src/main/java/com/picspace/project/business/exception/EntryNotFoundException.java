package com.picspace.project.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntryNotFoundException extends ResponseStatusException {
    public EntryNotFoundException(){
        super(HttpStatus.NOT_FOUND);
    }
}
