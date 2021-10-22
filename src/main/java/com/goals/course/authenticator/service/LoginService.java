package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dto.LoginRequest;
import com.goals.course.authenticator.dto.LoginResponse;
import org.springframework.security.authentication.BadCredentialsException;

public interface LoginService {
    LoginResponse login(final LoginRequest request) throws BadCredentialsException;
}
