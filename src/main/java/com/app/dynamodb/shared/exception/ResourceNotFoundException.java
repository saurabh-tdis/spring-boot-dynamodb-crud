package com.app.dynamodb.shared.exception;

import org.springframework.modulith.NamedInterface;

@NamedInterface
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}