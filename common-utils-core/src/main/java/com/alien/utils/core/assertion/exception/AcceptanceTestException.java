package com.alien.utils.core.assertion.exception;

public class AcceptanceTestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AcceptanceTestException(String description) {
        super(description);
    }
    
    public AcceptanceTestException(Throwable throwable) {
        super(throwable);
    }  
    
    public AcceptanceTestException(String description, Throwable throwable) {
        super(description, throwable);
    }        
    
}
