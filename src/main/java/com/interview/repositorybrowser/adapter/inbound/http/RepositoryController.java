package com.interview.repositorybrowser.adapter.inbound.http;

import com.interview.repositorybrowser.domain.exception.UnsupportedMediaTypeException;
import com.interview.repositorybrowser.domain.model.Repository;
import com.interview.repositorybrowser.domain.model.RepositoryType;
import com.interview.repositorybrowser.domain.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class RepositoryController {
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
    @Autowired
    private RepositoryService repositoryService;

    @GetMapping(value = "/repositories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Repository> listRepositories(@RequestHeader("Content-type") String contentType,
                                             @RequestParam String userLogin,
                                             @RequestParam RepositoryType repositoryType) {
        if (!isContentTypeJson(contentType)) {
            throw new UnsupportedMediaTypeException("Unsupported media type!");
        }
        log.info(String.format("Listing repositories for [userLogin=%s, repositoryType=%s]", userLogin, repositoryType));

        List<Repository> repositories = repositoryService.listRepositories(userLogin, repositoryType);

        log.info(String.format("Repositories found [userLogin=%s, count=%d]", userLogin, repositories.size()));
        return repositories;
    }

    private static boolean isContentTypeJson(String contentType) {
        return APPLICATION_JSON.equals(contentType) || APPLICATION_JSON_UTF8.equals(contentType);
    }
}
