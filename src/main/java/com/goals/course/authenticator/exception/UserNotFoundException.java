package com.goals.course.authenticator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final UUID userId) {
        super(String.format("User with id '%s' not found!", userId));
    }

    public UserNotFoundException(final String message) {
        super(message);
    }
}
