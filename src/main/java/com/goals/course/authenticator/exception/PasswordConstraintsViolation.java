package com.goals.course.authenticator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PasswordConstraintsViolation extends RuntimeException {
    public PasswordConstraintsViolation(final String message) {
        super(message);
    }
}
