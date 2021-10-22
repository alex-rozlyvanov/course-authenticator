package com.goals.course.authenticator.controller;

import com.goals.course.authenticator.dto.*;
import org.springframework.http.ResponseEntity;

public interface AuthenticationController {
    ResponseEntity<LoginResponse> login(final LoginRequest request);

    LoginResponse signUp(final SignUpRequest request);

    TokenRefreshResponse refreshToken(final TokenRefreshRequest request);
}
