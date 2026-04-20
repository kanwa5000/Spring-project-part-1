package edu.bi.springdemo.entity.exception;

public class BookAlreadyExistsException extends InvalidRequestException {
    public BookAlreadyExistsException(String message) {
        super(message);
    }
}