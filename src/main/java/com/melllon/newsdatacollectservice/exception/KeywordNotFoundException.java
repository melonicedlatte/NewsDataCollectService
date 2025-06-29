package com.melllon.newsdatacollectservice.exception;

public class KeywordNotFoundException extends RuntimeException {
    
    public KeywordNotFoundException(String message) {
        super(message);
    }
    
    public KeywordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 