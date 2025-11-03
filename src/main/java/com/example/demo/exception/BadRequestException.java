package com.example.demo.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {

    public BadRequestException(String message) {
        super(BAD_REQUEST, message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(BAD_REQUEST, message, cause);
    }
}