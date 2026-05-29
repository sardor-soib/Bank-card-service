package com.example.bankcards.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return "ResourceNotFoundException: " + getMessage();
    }
}
