package com.ch.customexception;

public class UserRegistrationException extends Exception {
    public UserRegistrationException(String message) {
        super(message);
    }
}