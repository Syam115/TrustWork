package io.eikon.projectservice.exception;

public class InvalidProjectStateException extends RuntimeException {
    public InvalidProjectStateException(String message) {
        super(message);
    }

    public InvalidProjectStateException(String message, Throwable cause) {
        super(message, cause);
    }
}

