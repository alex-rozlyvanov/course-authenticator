package com.goals.course.authenticator.controller;

import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.service.UserRolesService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/authenticator/roles")
@AllArgsConstructor
public class RolesController {

    private final UserRolesService userRolesService;

    @GetMapping
    public Flux<RoleDTO> getAllRoles() {
        return userRolesService.getAllRoles();
    }
}
