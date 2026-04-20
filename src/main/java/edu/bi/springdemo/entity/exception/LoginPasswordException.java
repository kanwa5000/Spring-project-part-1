package edu.bi.springdemo.entity.exception;

public class LoginPasswordException extends RuntimeException {

    public LoginPasswordException(String message) {
        super(message);
    }
}