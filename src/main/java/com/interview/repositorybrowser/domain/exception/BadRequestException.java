package com.interview.repositorybrowser.domain.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(Exception ex) {
        super(ex);
    }

    public BadRequestException(String msg) {
        super(msg);
    }

    public BadRequestException(String msg, Exception ex) {
        super(msg, ex);
    }
}
