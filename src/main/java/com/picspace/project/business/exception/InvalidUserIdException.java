package com.picspace.project.business.exception;

public class InvalidUserIdException extends RuntimeException{
    public InvalidUserIdException(){
        super("An Invalid UserID has been supplied!");
    }
}
