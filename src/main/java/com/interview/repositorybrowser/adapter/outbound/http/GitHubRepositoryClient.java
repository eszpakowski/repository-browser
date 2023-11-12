package com.interview.repositorybrowser.adapter.outbound.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.interview.repositorybrowser.adapter.outbound.http.dto.BranchDTO;
import com.interview.repositorybrowser.adapter.outbound.http.dto.RepositoryDTO;
import com.interview.repositorybrowser.adapter.outbound.http.dto.RepositorySearchResponseDTO;
import com.interview.repositorybrowser.adapter.outbound.http.util.ResponseValidationUtils;
import com.interview.repositorybrowser.domain.exception.RepositoryBrowserRuntimeException;
import com.interview.repositorybrowser.domain.model.Branch;
import com.interview.repositorybrowser.domain.model.Repository;
import com.interview.repositorybrowser.port.RepositoryClient;
import com.interview.repositorybrowser.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class GitHubRepositoryClient implements RepositoryClient {
    private static final String BASE = "https://api.github.com";
    //By default, forks are not shown in repository search results
    private static final String REPOSITORY_SEARCH = BASE + "/search/repositories?q=";

    private static final String REPOS = "/repos";
    private static final String BRANCHES = "/branches";
    private static final String USER = "user:";
    private static final String PER_PAGE_100 = "&per_page=100";
    private static final String ORDER_DESC = "&order=desc";
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "application/json";
    private static final String F_SLASH = "/";

    @Autowired
    private final HttpClient httpClient;

    public GitHubRepositoryClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Fetches all non-fork GitHub repositories using repository search, then performs separate call per each repository
     * to list all its code branches. Merges results into a single response.
     *
     * @param userLogin owner of repository
     * @return list of all repositories owned by userLogin, includes branch information
     * <a href="https://docs.github.com/en/rest/search/search?apiVersion=2022-11-28#search-repositories">search-repositories</a>
     * <a href="https://docs.github.com/en/rest/branches/branches?apiVersion=2022-11-28#list-branches">list-branches</a>
     */
    @Override
    public List<Repository> fetchRepositories(String userLogin) {
        List<RepositoryDTO> repos = fetchRepositoriesSync(userLogin);
        Map<String, List<BranchDTO>> repoToBranchMap = fetchBranchesAsync(repos);

        return repos.stream()
                .map(repo -> toDomain(repoToBranchMap, repo))
                .toList();
    }

    private List<RepositoryDTO> fetchRepositoriesSync(String userLogin) {
        log.info(String.format("Fetching non-fork repositories [userLogin=%s]", userLogin));
        HttpRequest request = buildRepositorySearchRequest(userLogin);

        RepositorySearchResponseDTO responseDTO = performRepositorySearchRequest(request);

        List<RepositoryDTO> repositories = responseDTO.getRepositories();
        if (CollectionUtils.isEmpty(repositories)) {
            return Collections.emptyList();
        } else {
            return repositories;
        }
    }

    private RepositorySearchResponseDTO performRepositorySearchRequest(HttpRequest request) {
        log.debug(String.format("Sending HttpRequest [request=%s]", request));

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(request, ofString());
        } catch (Exception e) {
            throw new RepositoryBrowserRuntimeException("Problem occurred when fetching data!", e);
        }
        log.debug(String.format("HttpResponse retrieved [statusCode=%d, body=%s]",
                httpResponse.statusCode(), httpResponse.body()));

        ResponseValidationUtils.validateResponseCode(httpResponse);
        return JsonUtils.readValue(new TypeReference<>() {
        }, httpResponse.body());
    }

    private static HttpRequest buildRepositorySearchRequest(String userLogin) {
        try {
            return HttpRequest.newBuilder()
                    .header(ACCEPT, CONTENT_TYPE)
                    .uri(new URI(REPOSITORY_SEARCH + USER + userLogin + ORDER_DESC + PER_PAGE_100))
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(
                    String.format("Incorrect userLogin value! [userLogin=%s]", userLogin));
        }
    }

    private Map<String, List<BranchDTO>> fetchBranchesAsync(List<RepositoryDTO> repositories) {
        log.info(String.format("Fetching branches for the found repositories [count=%d]", repositories.size()));
        List<HttpRequest> requests = repositories.stream()
                .map(GitHubRepositoryClient::buildListBranchesRequest)
                .toList();

        List<HttpResponse<String>> responses = performListBranchesRequestsInParallel(requests);

        return parseAndCollectResultsToMap(responses);
    }

    private static HttpRequest buildListBranchesRequest(RepositoryDTO repo) {
        try {
            return HttpRequest.newBuilder()
                    .header(ACCEPT, CONTENT_TYPE)
                    .uri(new URI(BASE + REPOS + F_SLASH + repo.getOwnerLogin() + F_SLASH + repo.getName() + BRANCHES))
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            throw new RepositoryBrowserRuntimeException(
                    String.format("Incorrect repository value! [repo=%s]", repo));
        }
    }
    private List<HttpResponse<String>> performListBranchesRequestsInParallel(List<HttpRequest> requests) {
        List<CompletableFuture<HttpResponse<String>>> futuresList =
                requests.stream()
                        .map(request -> httpClient.sendAsync(request, ofString()))
                        .toList();

        return allAsList(futuresList).join();
    }

    private Map<String, List<BranchDTO>> parseAndCollectResultsToMap(List<HttpResponse<String>> responses) {
        return responses.stream()
                .peek(ResponseValidationUtils::validateResponseCode)
                .map(GitHubRepositoryClient::parseBranchList)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(
                        branch -> extractRepoName(branch.getUrl()),
                        Collectors.mapping(branch -> branch, toList())));
    }

    private static List<BranchDTO> parseBranchList(HttpResponse<String> httpResponse) {
        return JsonUtils.readValue(new TypeReference<>() {
        }, httpResponse.body());
    }

    private static String extractRepoName(String url) {
        //Repository name is the third section from the end
        // e.g. https://github.com/userLogin/repoName/branches/sha
        String[] urlSections = url.split(F_SLASH);
        return urlSections[urlSections.length - 3];
    }

    private <T> CompletableFuture<List<T>> allAsList(List<CompletableFuture<T>> futures) {
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList()
                );
    }

    private static Repository toDomain(Map<String, List<BranchDTO>> repoToBranchMap, RepositoryDTO dto) {
        List<BranchDTO> branchDTOs = repoToBranchMap.get(dto.getName());
        return new Repository(
                dto.getName(),
                dto.getUrl(),
                dto.getOwnerLogin(),
                toDomain(branchDTOs)
        );
    }

    private static List<Branch> toDomain(List<BranchDTO> branchDTOs) {
        if (CollectionUtils.isEmpty(branchDTOs)) {
            return Collections.emptyList();
        }
        return branchDTOs.stream()
                .map(branchdto -> new Branch(branchdto.getName(), branchdto.getCommitSha()))
                .toList();
    }
}