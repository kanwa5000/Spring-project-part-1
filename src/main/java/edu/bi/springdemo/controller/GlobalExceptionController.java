package edu.bi.springdemo.controller;

import edu.bi.springdemo.entity.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionController {

    private ApiError buildError(HttpStatus status, String message, HttpServletRequest request) {
        return new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            BookNotFoundException.class,
            LoanNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFound(RuntimeException e, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildError(HttpStatus.NOT_FOUND, e.getMessage(), request),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
            BookAlreadyExistsException.class,
            BookUnavailableException.class
    })
    public ResponseEntity<ApiError> handleConflict(RuntimeException e, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildError(HttpStatus.CONFLICT, e.getMessage(), request),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({
            InvalidRequestException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception e, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({
            LoginPasswordException.class,
            UnauthorizedException.class
    })
    public ResponseEntity<ApiError> handleUnauthorized(RuntimeException e, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildError(HttpStatus.UNAUTHORIZED, e.getMessage(), request),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleForbidden(AccessDeniedException e, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildError(HttpStatus.FORBIDDEN, "Access denied", request),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDatabaseConflict(DataIntegrityViolationException e, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildError(HttpStatus.CONFLICT, "Database constraint violation", request),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception e, HttpServletRequest request) {
        return new ResponseEntity<>(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}