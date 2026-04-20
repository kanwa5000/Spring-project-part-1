package edu.bi.springdemo.entity.exception;

public class LoanNotFoundException extends InvalidRequestException {
    public LoanNotFoundException(String message) {
        super(message);
    }
}