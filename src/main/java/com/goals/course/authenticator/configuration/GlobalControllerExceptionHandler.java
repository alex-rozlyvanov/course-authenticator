package com.goals.course.authenticator.configuration;

import com.goals.course.authenticator.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Clock;
import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class GlobalControllerExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(final HttpClientErrorException ex, WebRequest request) {
        final var path = ((ServletWebRequest) request).getRequest().getRequestURI();
        final var errorResponse = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now(clock))
                .status(ex.getStatusCode().value())
                .error(ex.getMessage())
                .path(path)
                .build();

        logError(ex, request);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private void logError(final Exception e, final WebRequest request) {
        log.error("Route: {}. Error message: {}", ((ServletWebRequest) request).getRequest().getRequestURI(), e.getMessage());
    }

}
