package com.interview.repositorybrowser.domain.exception;

public class UnsupportedMediaTypeException extends RuntimeException {
    public UnsupportedMediaTypeException(Exception ex) {
        super(ex);
    }

    public UnsupportedMediaTypeException(String msg) {
        super(msg);
    }

    public UnsupportedMediaTypeException(String msg, Exception ex) {
        super(msg, ex);
    }
}

