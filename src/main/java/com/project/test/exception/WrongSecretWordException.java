package com.project.test.exception;

public class WrongSecretWordException extends RuntimeException {
    public WrongSecretWordException(String message) {
        super(message);
    }
}
