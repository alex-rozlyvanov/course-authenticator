# Course-authenticator

## This is course-authenticator microservice of Course application.

### Main technologies:

- Gradle 7.2
- Java 17
- Postgres 14
- Spring Boot 2.6.3
- Spring Cloud 2021.0.0
- Netflix Eureka Client
- Flyway 7.7+
- Lombok 1.18+
- Slf4j

---

- Junit 5 + Mockito + Assertj
- TestContainers 1.17.2
- Spring Cloud Contract 3.1.3

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

**Push docker image to ECR:**
- aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/k7s0v3p5
- docker build -t course-authenticator:0.0.1 .
- docker tag course-authenticator:0.0.1 public.ecr.aws/k7s0v3p5/course-authenticator:0.0.1
- docker push public.ecr.aws/k7s0v3p5/course-authenticator:0.0.1

./gradlew clean build -x test -x integrationTest -x contractTest \
&& docker build -t course-authenticator:0.0.1 . \
&& docker tag course-authenticator:0.0.1 public.ecr.aws/k7s0v3p5/course-authenticator:0.0.1 \
&& docker push public.ecr.aws/k7s0v3p5/course-authenticator:0.0.1
