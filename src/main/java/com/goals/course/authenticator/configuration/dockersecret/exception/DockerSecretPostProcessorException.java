package com.goals.course.authenticator.configuration.dockersecret.exception;

public class DockerSecretPostProcessorException extends RuntimeException{
    public DockerSecretPostProcessorException(final Throwable cause) {
        super("Unable to read docker secrets", cause);
    }
}
