package org.example.demo.Exception;

public class UserFriendlyException extends RuntimeException {
    public UserFriendlyException(String message) {
        super(message);
    }

    public UserFriendlyException(String message, Throwable cause) {
        super(message, cause);
    }
}
