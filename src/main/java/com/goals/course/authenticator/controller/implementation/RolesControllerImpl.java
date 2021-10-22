package com.goals.course.authenticator.controller.implementation;

import com.goals.course.authenticator.controller.RolesController;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.service.UserRolesService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authenticator/roles")
@AllArgsConstructor
public class RolesControllerImpl implements RolesController {

    private final UserRolesService userRolesService;

    @GetMapping
    @Override
    public List<RoleDTO> getAllRoles() {
        return userRolesService.getAllRoles();
    }
}
