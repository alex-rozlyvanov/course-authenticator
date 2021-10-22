package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dto.LoginResponse;
import com.goals.course.authenticator.dto.SignUpRequest;

public interface SignUpService {
    LoginResponse signUp(final SignUpRequest request);
}
