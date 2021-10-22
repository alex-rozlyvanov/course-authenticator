package com.goals.course.authenticator.controller.implementation;

import com.goals.course.authenticator.controller.UserController;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.service.UserRolesService;
import com.goals.course.authenticator.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/authenticator/users")
@AllArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserRolesService userRolesService;
    private final UserService userService;

    @GetMapping("/{userId}")
    @Override
    public UserDTO getUserById(@PathVariable("userId") final UUID userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/{userId}/roles")
    @Override
    public List<RoleDTO> changeUserRoles(@PathVariable("userId") final UUID userId, @RequestBody final List<UUID> roleIds) {
        return userRolesService.changeUserRoles(userId, roleIds);
    }

    @GetMapping("/current")
    @Override
    public UserDTO getCurrentUser() {
        return userService.getCurrentUser();
    }
}
