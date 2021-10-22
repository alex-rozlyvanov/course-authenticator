package com.goals.course.authenticator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class LoginResponse {
    private final UserDTO user;
    private final String accessToken;
    private final String refreshToken;
}
