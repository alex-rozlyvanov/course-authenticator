package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.User;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SecurityService {
    public Mono<User> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(c -> (User) c.getAuthentication().getPrincipal());
    }
}
