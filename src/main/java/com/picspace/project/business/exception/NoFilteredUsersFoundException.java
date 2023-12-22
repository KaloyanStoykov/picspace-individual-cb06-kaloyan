package com.picspace.project.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoFilteredUsersFoundException extends ResponseStatusException {
    public NoFilteredUsersFoundException(){
        super(HttpStatus.NOT_FOUND, "No users have been found based on your search");
    }
}
