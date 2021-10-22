package com.goals.course.authenticator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Jacksonized
public class LoginRequest {

    @NotNull
    @Email
    private final String username;
    @NotNull
    private final String password;

}
