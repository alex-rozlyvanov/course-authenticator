package com.goals.course.authenticator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(final UUID userId) {
        super(String.format("User with id '%s' already exists!", userId));
    }

    public UserAlreadyExistsException(final String message) {
        super(message);
    }
}
