package com.melllon.newsdatacollectservice.exception;

public class NewsSiteAlreadyExistsException extends RuntimeException {
    
    public NewsSiteAlreadyExistsException(String message) {
        super(message);
    }
    
    public NewsSiteAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 