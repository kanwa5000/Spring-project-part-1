package edu.bi.springdemo.entity.exception;

public class BookUnavailableException extends InvalidRequestException {
    public BookUnavailableException(String message) {
        super(message);
    }
}