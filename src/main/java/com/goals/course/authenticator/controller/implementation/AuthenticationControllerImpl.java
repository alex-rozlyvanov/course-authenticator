package com.goals.course.authenticator.controller.implementation;

import com.goals.course.authenticator.controller.AuthenticationController;
import com.goals.course.authenticator.dto.*;
import com.goals.course.authenticator.service.LoginService;
import com.goals.course.authenticator.service.RefreshTokenService;
import com.goals.course.authenticator.service.SecurityService;
import com.goals.course.authenticator.service.SignUpService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/authenticator")
@AllArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityService securityService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid final LoginRequest request) {
        try {
            return ResponseEntity.ok().body(loginService.login(request));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public String logout() {
        final var currentUserId = securityService.getCurrentUser().getId();
        refreshTokenService.deleteRefreshTokenByUserId(currentUserId);
        return "Session finished";
    }

    @PostMapping("/signup")
    public LoginResponse signUp(@RequestBody @Valid final SignUpRequest request) {
        return signUpService.signUp(request);
    }

    @PostMapping("/refresh")
    public TokenRefreshResponse refreshToken(@RequestBody @Valid final TokenRefreshRequest request) {
        final var requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.refreshToken(requestRefreshToken);
    }

}
