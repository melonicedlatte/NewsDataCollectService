package com.melllon.newsdatacollectservice.exception;

public class NewsSiteNotFoundException extends RuntimeException {
    
    public NewsSiteNotFoundException(String message) {
        super(message);
    }
    
    public NewsSiteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 