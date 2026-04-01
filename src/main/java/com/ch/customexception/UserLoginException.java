package com.ch.customexception;

public class UserLoginException extends Exception {
    public UserLoginException(String message) {
        super(message);
    }
}
