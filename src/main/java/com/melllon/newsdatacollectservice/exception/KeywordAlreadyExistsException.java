package com.melllon.newsdatacollectservice.exception;

public class KeywordAlreadyExistsException extends RuntimeException {
    
    public KeywordAlreadyExistsException(String message) {
        super(message);
    }
    
    public KeywordAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 