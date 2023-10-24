package com.interview.repositorybrowser.domain.model;

import java.util.List;

public record Repository(String name, String url, String ownerLogin, List<Branch> branches) {
    public Repository {
        branches = List.copyOf(branches); //defensive copy
    }
}
