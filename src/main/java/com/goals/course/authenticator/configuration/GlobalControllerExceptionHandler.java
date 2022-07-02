package com.goals.course.authenticator.configuration;

import com.goals.course.authenticator.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerWebExchange;

import java.time.Clock;
import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
@RestControllerAdvice
@AllArgsConstructor
public class GlobalControllerExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(HttpClientErrorException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(final HttpClientErrorException ex, ServerWebExchange exchange) {
        final var path = exchange.getRequest().getPath().value();
        final var errorResponse = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now(clock))
                .status(ex.getStatusCode().value())
                .error(ex.getMessage())
                .path(path)
                .build();

        logError(ex, exchange);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConflict(final DataIntegrityViolationException ex, ServerWebExchange exchange) {
        final var path = exchange.getRequest().getPath().value();
        final var errorResponse = ErrorResponse.builder()
                .timestamp(ZonedDateTime.now(clock))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Data integrity violation exception occurred")
                .path(path)
                .build();

        logError(ex, exchange);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private void logError(final Exception e, final ServerWebExchange exchange) {
        log.error("Route: {}. Error message: {}", exchange.getRequest().getPath().value(), e.getMessage());
    }

}
