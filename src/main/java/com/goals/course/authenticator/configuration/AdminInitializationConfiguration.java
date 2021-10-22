package com.goals.course.authenticator.configuration;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.enums.Roles;
import com.goals.course.authenticator.dao.repository.RoleRepository;
import com.goals.course.authenticator.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializationConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${app.admin.password}")
    private String defaultAdminPass;
    @Value("${app.admin.username}")
    private String defaultAdminUsername;
    @Value("${app.admin.firstname}")
    private String defaultAdminFirstname;
    @Value("${app.admin.lastname}")
    private String defaultAdminLastname;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        final var roles = initializeRoles();
        initializeAdminUser(roles);
    }

    private List<Role> initializeRoles() {
        final var existingRoles = roleRepository.findAll();

        return Arrays.stream(Roles.values())
                .map(role -> saveOrGetRole(role.name(), existingRoles))
                .toList();
    }

    private Role saveOrGetRole(final String roleTitle, final List<Role> existingRoles) {
        return existingRoles
                .stream()
                .filter(r -> roleTitle.equals(r.getTitle()))
                .findFirst()
                .orElseGet(() -> roleRepository.save(new Role().setTitle(roleTitle)));
    }

    private void initializeAdminUser(final List<Role> roles) {
        final var user = userRepository.findByUsername(defaultAdminUsername);

        if (user.isPresent()) {
            log.info("Admin already exists");
        } else {
            userRepository.save(buildAdminUser(roles));
            log.info("Admin added successfully");
        }
    }

    private User buildAdminUser(final List<Role> roles) {
        return new User()
                .setEnabled(true)
                .setFirstName(defaultAdminFirstname)
                .setLastName(defaultAdminLastname)
                .setUsername(defaultAdminUsername)
                .setPassword(passwordEncoder.encode(defaultAdminPass))
                .setRoles(roles);
    }
}
