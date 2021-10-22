package com.goals.course.authenticator.dto;

import com.goals.course.authenticator.exception.PasswordConstraintsViolation;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Jacksonized
public class SignUpRequest {

    @NotNull
    private final String firstName;

    @NotNull
    private final String lastName;

    @NotNull
    @Email
    private final String username;

    @NotNull
    private final String password;

    @SuppressWarnings("unused")
    public static class SignUpRequestBuilder {
        @SuppressWarnings("FieldCanBeLocal")
        private String password;

        public SignUpRequestBuilder password(final String password) {
            if (passwordIsValid(password)) {
                this.password = password;
            } else {
                throwPasswordConstraintsViolation();
            }
            return this;
        }

        private boolean passwordIsValid(String password) {
            return StringUtils.hasText(password)
                    && atLeastOneDigit(password)
                    && atLeastOneLowercaseLetter(password)
                    && atLeastOneUppercaseLetter(password)
                    && atLeastOneSpecialCharacter(password)
                    && atLeast8CharactersButNoMoreThan32(password);
        }

        private void throwPasswordConstraintsViolation() {
            final var message = """
                    Password should have:
                        - at least one digit
                        - at least one lowercase letter
                        - at least one uppercase letter
                        - at least one special character
                        - must be at least 8 characters long, but no more than 32
                    """;

            throw new PasswordConstraintsViolation(message);
        }

        private boolean atLeast8CharactersButNoMoreThan32(final String password) {
            return password.matches("^.{8,32}$");
        }

        private boolean atLeastOneSpecialCharacter(final String password) {
            return password.matches(".*[*.!@$%^&(){}\\[\\]:;<>,?/~_+\\-=|\\\\].*");
        }

        private boolean atLeastOneUppercaseLetter(final String password) {
            return password.matches(".*[A-Z].*");
        }

        private boolean atLeastOneLowercaseLetter(final String password) {
            return password.matches(".*[a-z].*");
        }

        private boolean atLeastOneDigit(final String password) {
            return password.matches(".*[0-9].*");
        }

    }
}
