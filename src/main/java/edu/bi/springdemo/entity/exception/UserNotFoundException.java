package edu.bi.springdemo.entity.exception;

public class UserNotFoundException extends InvalidRequestException {
    public UserNotFoundException(String message) {
        super(message);
    }
}