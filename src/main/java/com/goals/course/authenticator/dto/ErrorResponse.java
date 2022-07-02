package com.goals.course.authenticator.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;

@Getter
@Data
@Builder
@Jacksonized
public final class ErrorResponse {
    private final ZonedDateTime timestamp;
    private final int status;
    private final String error;
    private final String path;
    private final String requestId;
}
