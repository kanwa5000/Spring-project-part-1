package edu.bi.springdemo.entity.exception;

public class BookNotFoundException extends InvalidRequestException {
  public BookNotFoundException(String message) {
    super(message);
  }
}