package com.project.test.exception;

public class NotAccountOwnerException extends RuntimeException {
    public NotAccountOwnerException(String message) {
        super(message);
    }
}
