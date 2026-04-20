package edu.bi.springdemo.entity.exception;

public class UserAlreadyExistsException extends InvalidRequestException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}