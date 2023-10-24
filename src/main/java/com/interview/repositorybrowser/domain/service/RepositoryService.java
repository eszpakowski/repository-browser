package com.interview.repositorybrowser.domain.service;

import com.interview.repositorybrowser.domain.model.Repository;
import com.interview.repositorybrowser.domain.model.RepositoryType;
import com.interview.repositorybrowser.port.RepositoryClient;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryService {
    @Autowired
    private RepositoryClient repositoryClient;

    public List<Repository> listRepositories(String userLogin, RepositoryType repository) {
        if (StringUtils.isEmpty(userLogin) || repository == null) {
            throw new IllegalArgumentException(
                    String.format("[userLogin=%s, repository=%s]", userLogin, repository));
        }

        switch (repository) {
            case GITHUB -> {
                return repositoryClient.fetchRepositories(userLogin);
            }
            default -> throw new IllegalArgumentException("Unsupported repository parameter!");
        }
    }
}