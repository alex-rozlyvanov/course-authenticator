package com.goals.course.authenticator.controller;

import com.goals.course.authenticator.dto.*;
import com.goals.course.authenticator.service.LoginService;
import com.goals.course.authenticator.service.RefreshTokenService;
import com.goals.course.authenticator.service.SecurityService;
import com.goals.course.authenticator.service.SignUpService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/authenticator")
@AllArgsConstructor
public class AuthenticationController {
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityService securityService;

    @PostMapping("/login")
    public Mono<LoginResponse> login(@RequestBody @Valid final LoginRequest request) {
        return loginService.login(request);
    }

    @PostMapping("/logout")
    public Mono<String> logout() {
        return securityService.getCurrentUser()
                .flatMap(currentUser -> {
                    log.info("User '{}' logout", currentUser.getId());
                    return refreshTokenService.deleteRefreshTokenByUserId(currentUser.getId());
                })
                .map(r -> "Session finished");
    }

    @PostMapping("/signup")
    public Mono<LoginResponse> signUp(@RequestBody @Valid final SignUpRequest request) {
        return signUpService.signUp(request);
    }

    @PostMapping("/refresh")
    public Mono<TokenRefreshResponse> refreshToken(@RequestBody @Valid final TokenRefreshRequest request) {
        return refreshTokenService.refreshToken(request.getRefreshToken());
    }

}
