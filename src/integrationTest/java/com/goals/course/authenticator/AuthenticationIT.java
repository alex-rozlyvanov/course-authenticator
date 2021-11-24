package com.goals.course.authenticator;


import com.goals.course.authenticator.dao.enums.Roles;
import com.goals.course.authenticator.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@AutoConfigureWebClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(initializers = AuthenticationIT.Initializer.class)
public class AuthenticationIT {

    @ClassRule
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.0")
            .withDatabaseName("course_authenticator")
            .withUsername("sa")
            .withPassword("sa");

    @Autowired
    private WebTestClient webTestClient;

    @Value("${app.admin.password}")
    private String defaultAdminPass;
    @Value("${app.admin.username}")
    private String defaultAdminUsername;

    @Test
    void shouldBeFunctionalityForRegistrationInTheSystem() {
        // GIVEN
        final var signUpRequest = SignUpRequest.builder()
                .username("test+user@gmail.com")
                .firstName("testFirstName")
                .lastName("testLastName123")
                .password("SomeValidSecurePass123+")
                .build();

        // WHEN
        final var loginResponse = post("/api/authenticator/signup", signUpRequest, LoginResponse.class);

        // THEN
        final var newUser = get("/api/authenticator/users/current", loginResponse.getAccessToken(), UserDTO.class);
        assertThat(newUser).isPresent();
    }

    @Test
    void adminUserShouldBePredefined() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username(defaultAdminUsername)
                .password(defaultAdminPass)
                .build();

        // WHEN
        final var loginResponse = post("/api/authenticator/login", loginRequest, LoginResponse.class);

        // THEN
        final var adminUser = get("/api/authenticator/users/current", loginResponse.getAccessToken(), UserDTO.class);
        assertThat(adminUser).isPresent();
        assertThat(adminUser.get().getRoles())
                .extracting(RoleDTO::getTitle)
                .contains(Roles.ADMIN.name());
    }

    @Test
    void userShouldBeAbleToRefreshToken() {
        // GIVEN
        final var adminLoginResponse = loginAsAdmin();
        final var tokenRefreshRequest = TokenRefreshRequest.builder()
                .refreshToken(adminLoginResponse.getRefreshToken())
                .build();

        // WHEN
        final var result = post("/api/authenticator/refresh", tokenRefreshRequest, TokenRefreshResponse.class);

        // THEN
        webTestClient.get().uri("/api/authenticator/users/current")
                .header(AUTHORIZATION, "Bearer %s".formatted(result.getAccessToken()))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void adminShouldBeAbleToAssignARoleForANewUser() {
        // GIVEN
        final var newUserLoginResponse = signUpNewUser();
        final var newUser = newUserLoginResponse.getUser();
        assertThat(newUser.getRoles()).isEmpty();

        final var adminLoginResponse = loginAsAdmin();
        final var roleIds = get("/api/authenticator/roles", adminLoginResponse.getAccessToken(), new ParameterizedTypeReference<List<RoleDTO>>() {
        })
                .map(r -> r.stream().map(RoleDTO::getId).collect(Collectors.toList()))
                .orElseThrow();

        final var uri = "/api/authenticator/users/%s/roles".formatted(newUser.getId());

        // WHEN
        post(uri, roleIds, adminLoginResponse.getAccessToken());

        // THEN
        final var updatedNewUser = get("/api/authenticator/users/current", newUserLoginResponse.getAccessToken(), UserDTO.class);
        assertThat(updatedNewUser).isPresent();
        assertThat(updatedNewUser.get().getRoles()).hasSize(roleIds.size());

        final var newUserActualRoleIds = updatedNewUser.get().getRoles().stream().map(RoleDTO::getId).collect(Collectors.toList());
        assertThat(newUserActualRoleIds).containsOnlyOnceElementsOf(roleIds);
    }

    private LoginResponse signUpNewUser() {
        final var signUpRequest = SignUpRequest.builder()
                .username("test+username@gmail.com")
                .firstName("testFirstName")
                .lastName("testLastName123")
                .password("SomeValidSecurePass123+")
                .build();


        return post("/api/authenticator/signup", signUpRequest, LoginResponse.class);
    }

    private LoginResponse loginAsAdmin() {
        final var loginRequest = LoginRequest.builder()
                .password(defaultAdminPass)
                .username(defaultAdminUsername)
                .build();

        return webTestClient.post().uri("/api/authenticator/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectBody(LoginResponse.class)
                .returnResult().getResponseBody();
    }

    private <T> Optional<T> get(final String uri, final String accessToken, final Class<T> responseType) {
        final var responseBody = webTestClient.get().uri(uri)
                .header(AUTHORIZATION, "Bearer %s".formatted(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(responseType)
                .returnResult()
                .getResponseBody();
        return ofNullable(responseBody);
    }

    private <T> Optional<T> get(final String uri, final String accessToken, final ParameterizedTypeReference<T> responseType) {
        final var responseBody = webTestClient.get().uri(uri)
                .header(AUTHORIZATION, "Bearer %s".formatted(accessToken))
                .exchange()
                .expectStatus().isOk()
                .expectBody(responseType)
                .returnResult()
                .getResponseBody();
        return ofNullable(responseBody);
    }

    private void post(final String uri, final Object body, final String accessToken) {
        webTestClient.post().uri(uri)
                .header(AUTHORIZATION, "Bearer %s".formatted(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }

    private <T> T post(final String uri, final Object body, final Class<T> responseType) {
        return webTestClient.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(responseType)
                .returnResult().getResponseBody();
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgreSQLContainer.start();
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
