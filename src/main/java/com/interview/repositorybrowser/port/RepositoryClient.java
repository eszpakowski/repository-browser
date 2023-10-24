package com.interview.repositorybrowser.port;

import com.interview.repositorybrowser.domain.model.Repository;

import java.util.List;

public interface RepositoryClient {
    List<Repository> fetchRepositories(String userLogin);
}
