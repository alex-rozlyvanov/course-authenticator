package com.goals.course.authenticator.configuration;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.enums.Roles;
import com.goals.course.authenticator.dao.repository.RoleRepository;
import com.goals.course.authenticator.dao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializationConfiguration {

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

    @EventListener
    public Mono<Void> onApplicationEvent(final ContextRefreshedEvent event) {
        return initializeRoles()
                .flatMap(this::initializeAdminUser)
                .flatMap(result -> Mono.empty());
    }

    private Mono<List<Role>> initializeRoles() {
        return roleRepository.findAll()
                .collectList()
                .map(this::getRolesToSave)
                .flatMapIterable(list -> list)
                .flatMap(role -> roleRepository.save(new Role().setTitle(role.getTitle())))
                .switchIfEmpty(Flux.fromStream(this::buildAllRoles))
                .collectList();
    }

    private Stream<Role> buildAllRoles() {
        return Arrays.stream(Roles.values())
                .map(role -> new Role().setTitle(role.name()));
    }

    private List<Role> getRolesToSave(final List<Role> existingRoles) {
        return Arrays.stream(Roles.values())
                .filter(role -> roleExists(existingRoles, role))
                .map(role -> new Role().setTitle(role.name()))
                .toList();
    }

    private boolean roleExists(List<Role> existingRoles, Roles role) {
        return existingRoles.stream().noneMatch(r -> role.name().equals(r.getTitle()));
    }

    private Mono<User> initializeAdminUser(final List<Role> roles) {
        return userRepository.findByUsername(defaultAdminUsername)
                .map(user -> {
                    log.info("Admin already exists");
                    return user;
                })
                .switchIfEmpty(Mono.defer(() -> createAdmin(roles)));
    }

    private Mono<User> createAdmin(List<Role> roles) {
        return userRepository.save(buildAdminUser(roles))
                .map(user -> {
                    log.info("Admin added successfully. '{}'", user.getId());
                    return user;
                });
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
