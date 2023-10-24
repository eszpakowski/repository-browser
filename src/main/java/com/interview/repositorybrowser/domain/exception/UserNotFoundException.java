package com.interview.repositorybrowser.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Exception ex) {
        super(ex);
    }

    public UserNotFoundException(String msg) {
        super(msg);
    }

    public UserNotFoundException(String msg, Exception ex) {
        super(msg, ex);
    }
}
