package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.User;

public interface SecurityService {
    User getCurrentUser();
}
