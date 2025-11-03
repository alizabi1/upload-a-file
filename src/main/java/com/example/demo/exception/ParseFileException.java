package com.example.demo.exception;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class ParseFileException extends ResponseStatusException {

    public ParseFileException(String message, Throwable cause) {
        super(INTERNAL_SERVER_ERROR, message, cause);
    }
}