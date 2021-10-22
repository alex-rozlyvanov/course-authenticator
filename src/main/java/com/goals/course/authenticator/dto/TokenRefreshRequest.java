package com.goals.course.authenticator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@Jacksonized
public class TokenRefreshRequest {
    @NotBlank
    private final String refreshToken;
}
