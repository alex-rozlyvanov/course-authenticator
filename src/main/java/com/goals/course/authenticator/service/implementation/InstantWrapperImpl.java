package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.service.InstantWrapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InstantWrapperImpl implements InstantWrapper {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
