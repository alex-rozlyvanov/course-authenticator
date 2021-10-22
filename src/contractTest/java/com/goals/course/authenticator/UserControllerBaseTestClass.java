package com.goals.course.authenticator;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.enums.Roles;
import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.service.JwtTokenService;
import com.goals.course.authenticator.service.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("contract-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMessageVerifier
@AutoConfigureMockMvc
@ContextConfiguration(initializers = UserControllerBaseTestClass.Initializer.class)
public class UserControllerBaseTestClass {

    @ClassRule
    public static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.0")
            .withDatabaseName("course_authenticator")
            .withUsername("sa")
            .withPassword("sa");
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService mockUserService;
    @MockBean
    private JwtTokenService mockJwtTokenService;
    @MockBean
    private UserRepository mockUserRepository;

    @BeforeEach
    public void setup() {
        init();
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    private void init() {
        final var userDTO = getUserDTO();
        final var user = getUser();

        when(mockUserService.getCurrentUser()).thenReturn(userDTO);
        when(mockUserService.getUserById(any())).thenReturn(userDTO);
        when(mockUserRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(mockJwtTokenService.validate(any())).thenReturn(true);
    }

    private UserDTO getUserDTO() {
        final List<RoleDTO> roles = buildRoles();

        return UserDTO.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .enabled(true)
                .username("test+user@gmail.com")
                .firstName("firstname")
                .lastName("lastName")
                .roles(roles)
                .build();
    }

    public static List<RoleDTO> buildRoles() {
        return Arrays.stream(Roles.values())
                .map(role -> RoleDTO.builder()
                        .id(UUID.randomUUID())
                        .title(role.name())
                        .build())
                .toList();
    }

    private User getUser() {
        final var role = new Role()
                .setId(UUID.randomUUID())
                .setTitle(Roles.ADMIN.name());
        final var roles1 = List.of(role);

        return new User()
                .setUsername("test+user@gmail.com")
                .setRoles(roles1);
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
