# Course-authenticator

## This is course-authenticator microservice of Course application.

### Main technologies:

- Gradle 7.2
- Java 17
- Postgres 14
- Spring Boot 2.5.5
- Spring Cloud 2020.0.3
- Netflix Eureka Client
- Flyway 7.7+
- Lombok 1.18+
- Slf4j

---

- Junit 5 + Mockito + Assertj
- TestContainers 1.16
- Spring Cloud Contract 3.0.4

### Responsibilities:

- SignUp
- Login
- Logout
- Refresh token
- Get current user profile/data
- Get user profile/data
- Change user roles
- Get existing roles

## How to build:

**Regular build:** `./gradlew clean build`

**Without tests build:** `./gradlew clean build -x test -x integrationTest -x contractTest`

**Run local in docker
build:** `./gradlew clean build -x test -x integrationTest -x contractTest && docker-compose up --build`