package com.interview.repositorybrowser.domain.exception;

public class RepositoryBrowserRuntimeException extends RuntimeException {
    public RepositoryBrowserRuntimeException(Exception ex) {
        super(ex);
    }

    public RepositoryBrowserRuntimeException(String msg) {
        super(msg);
    }

    public RepositoryBrowserRuntimeException(String msg, Exception ex) {
        super(msg, ex);
    }
}
