package com.goals.course.authenticator.dto;

import com.goals.course.authenticator.exception.PasswordConstraintsViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SignUpRequestTest {

    @Test
    void setPassword_validPassword_checkResult() {
        // GIVEN

        // WHEN
        final var result = SignUpRequest.builder().password("P@assw0rd").build();

        // THEN
        assertThat(result.getPassword()).isEqualTo("P@assw0rd");
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void setPassword_inValidPassword_exceptionThrown(final String invalidPassword) {
        // GIVEN
        final var signUpRequestBuilder = SignUpRequest.builder();

        // WHEN
        final var expectedException = assertThrows(
                PasswordConstraintsViolation.class,
                () -> signUpRequestBuilder.password(invalidPassword)
        );

        // THEN
        final var expectedMessage = """
                Password should have:
                    - at least one digit
                    - at least one lowercase letter
                    - at least one uppercase letter
                    - at least one special character
                    - must be at least 8 characters long, but no more than 32
                """;
        assertThat(expectedException.getMessage())
                .isEqualTo(expectedMessage);
    }

    private static Stream<String> invalidPasswords() {
        return Stream.of(
                null,
                "",
                "P@sw0rd",
                "password",
                "Password",
                "Passw0rd",
                "P@ssword",
                "P@ssword_1s_tooooooooooooooo_long"
        );
    }
}
