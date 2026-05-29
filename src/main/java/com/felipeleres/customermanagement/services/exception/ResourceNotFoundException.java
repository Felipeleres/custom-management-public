package com.felipeleres.customermanagement.services.exception;

public class ResourceNotFoundException extends RuntimeException {


    public ResourceNotFoundException(String message) {
        super(message);
    }
}
