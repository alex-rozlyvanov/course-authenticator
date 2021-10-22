package com.goals.course.authenticator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CourseAuthenticatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseAuthenticatorApplication.class, args);
    }

}
