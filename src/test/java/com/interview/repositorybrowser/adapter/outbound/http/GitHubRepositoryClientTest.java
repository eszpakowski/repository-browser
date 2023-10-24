package com.interview.repositorybrowser.adapter.outbound.http;

import com.interview.repositorybrowser.domain.exception.RepositoryBrowserRuntimeException;
import com.interview.repositorybrowser.domain.model.Branch;
import com.interview.repositorybrowser.domain.model.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GitHubRepositoryClientTest {
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "application/json";
    private static final String REPO_NAME = "repo-name";
    private static final String USER_LOGIN = "userLogin";
    private static final String BRANCH_NAME = "master";
    private static final String SHA = "99ee262e6df4f25e6b73594f1c43fa01410e7e";
    private static final String BASE = "https://api.github.com";
    public static final String REPOSITORIES_URI = BASE + "/search/repositories?q=user:" + USER_LOGIN + "&order=desc&per_page=100";
    private static final String REPOSITORIES_EMPTY_BODY = "{\"total_count\":0,\"incomplete_results\":false,\"items\":[]}";
    private static final String REPOSITORIES_EXAMPLE_BODY = "{\"total_count\":1,\"incomplete_results\":false,\"items\":[{\"id\":33535160,\"node_id\":\"MDEwOlJlcG9aXRvcnkzMzUzNTIxNjA=\",\"name\":\"repo-name\",\"full_name\":\"userLogin/repo-name\",\"private\":false,\"owner\":{\"login\":\"userLogin\",\"id\":424407,\"node_id\":\"MDQ6VXNlcQyNDQwNw==\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/424407?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/userLogin\",\"html_url\":\"https://github.com/userLogin\",\"followers_url\":\"https://api.github.com/users/userLogin/followers\",\"following_url\":\"https://api.github.com/users/userLogin/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/userLogin/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/userLogin/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/userLogin/subscriptions\",\"organizations_url\":\"https://api.github.com/users/userLogin/orgs\",\"repos_url\":\"https://api.github.com/users/userLogin/repos\",\"events_url\":\"https://api.github.com/users/userLogin/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/userLogin/received_events\",\"type\":\"User\",\"site_admin\":false},\"html_url\":\"https://github.com/userLogin/repo-name\",\"description\":\"Exercises from Web Development with Clojure (3rd edition) \",\"fork\":false,\"url\":\"https://api.github.com/repos/userLogin/repo-name\",\"forks_url\":\"https://api.github.com/repos/userLogin/repo-name/forks\",\"keys_url\":\"https://api.github.com/repos/userLogin/repo-name/keys{/key_id}\",\"collaborators_url\":\"https://api.github.com/repos/userLogin/repo-name/collaborators{/collaborator}\",\"teams_url\":\"https://api.github.com/repos/userLogin/repo-name/teams\",\"hooks_url\":\"https://api.github.com/repos/userLogin/repo-name/hooks\",\"issue_events_url\":\"https://api.github.com/repos/userLogin/repo-name/issues/events{/number}\",\"events_url\":\"https://api.github.com/repos/userLogin/repo-name/events\",\"assignees_url\":\"https://api.github.com/repos/userLogin/repo-name/assignees{/user}\",\"branches_url\":\"https://api.github.com/repos/userLogin/repo-name/branches{/branch}\",\"tags_url\":\"https://api.github.com/repos/userLogin/repo-name/tags\",\"blobs_url\":\"https://api.github.com/repos/userLogin/repo-name/git/blobs{/sha}\",\"git_tags_url\":\"https://api.github.com/repos/userLogin/repo-name/git/tags{/sha}\",\"git_refs_url\":\"https://api.github.com/repos/userLogin/repo-name/git/refs{/sha}\",\"trees_url\":\"https://api.github.com/repos/userLogin/repo-name/git/trees{/sha}\",\"statuses_url\":\"https://api.github.com/repos/userLogin/repo-name/statuses/{sha}\",\"languages_url\":\"https://api.github.com/repos/userLogin/repo-name/languages\",\"stargazers_url\":\"https://api.github.com/repos/userLogin/repo-name/stargazers\",\"contributors_url\":\"https://api.github.com/repos/userLogin/repo-name/contributors\",\"subscribers_url\":\"https://api.github.com/repos/userLogin/repo-name/subscribers\",\"subscription_url\":\"https://api.github.com/repos/userLogin/repo-name/subscription\",\"commits_url\":\"https://api.github.com/repos/userLogin/repo-name/commits{/sha}\",\"git_commits_url\":\"https://api.github.com/repos/userLogin/repo-name/git/commits{/sha}\",\"comments_url\":\"https://api.github.com/repos/userLogin/repo-name/comments{/number}\",\"issue_comment_url\":\"https://api.github.com/repos/userLogin/repo-name/issues/comments{/number}\",\"contents_url\":\"https://api.github.com/repos/userLogin/repo-name/contents/{+path}\",\"compare_url\":\"https://api.github.com/repos/userLogin/repo-name/compare/{base}...{head}\",\"merges_url\":\"https://api.github.com/repos/userLogin/repo-name/merges\",\"archive_url\":\"https://api.github.com/repos/userLogin/repo-name/{archive_format}{/ref}\",\"downloads_url\":\"https://api.github.com/repos/userLogin/repo-name/downloads\",\"issues_url\":\"https://api.github.com/repos/userLogin/repo-name/issues{/number}\",\"pulls_url\":\"https://api.github.com/repos/userLogin/repo-name/pulls{/number}\",\"milestones_url\":\"https://api.github.com/repos/userLogin/repo-name/milestones{/number}\",\"notifications_url\":\"https://api.github.com/repos/userLogin/repo-name/notifications{?since,all,participating}\",\"labels_url\":\"https://api.github.com/repos/userLogin/repo-name/labels{/name}\",\"releases_url\":\"https://api.github.com/repos/userLogin/repo-name/releases{/id}\",\"deployments_url\":\"https://api.github.com/repos/userLogin/repo-name/deployments\",\"created_at\":\"2021-02-02T16:27:04Z\",\"updated_at\":\"2022-03-05T12:05:02Z\",\"pushed_at\":\"2021-02-11T21:53:35Z\",\"git_url\":\"git://github.com/userLogin/repo-name.git\",\"ssh_url\":\"git@github.com:userLogin/repo-name.git\",\"clone_url\":\"https://github.com/userLogin/repo-name.git\",\"svn_url\":\"https://github.com/userLogin/repo-name\",\"homepage\":\"\",\"size\":70,\"stargazers_count\":1,\"watchers_count\":1,\"language\":\"Clojure\",\"has_issues\":true,\"has_projects\":true,\"has_downloads\":true,\"has_wiki\":true,\"has_pages\":false,\"has_discussions\":false,\"forks_count\":1,\"mirror_url\":null,\"archived\":false,\"disabled\":false,\"open_issues_count\":0,\"license\":null,\"allow_forking\":true,\"is_template\":false,\"web_commit_signoff_required\":false,\"topics\":[],\"visibility\":\"public\",\"forks\":1,\"open_issues\":0,\"watchers\":1,\"default_branch\":\"master\",\"score\":1.0}]}";
    private static final String BRANCHES_URI = BASE + "/repos/" + USER_LOGIN + "/repo-name/branches";
    private static final String BRANCHES_EMPTY_BODY = "[]";
    private static final String BRANCHES_EXAMPLE_BODY = "[{\"name\":\"master\",\"commit\":{\"sha\":\"99ee262e6df4f25e6b73594f1c43fa01410e7e\",\"url\":\"https://api.github.com/repos/userLogin/repo-name/commits/99ee262e6df4f25e6b73594f1c43fa01410e7e\"},\"protected\":false}]";

    @Mock
    HttpResponse<String> mockResponseSync;
    @Mock
    HttpResponse<String> mockResponseAsync;

    @Mock
    HttpClient httpClient;
    @InjectMocks
    GitHubRepositoryClient underTest;

    @Test
    void givenException_shouldThrowRepositoryBrowserRuntimeException() throws Exception {
        //given
        given(httpClient.send(any(HttpRequest.class), any(ofString().getClass())))
                .willThrow(new IOException());

        //when
        assertThrows(RepositoryBrowserRuntimeException.class,
                () -> underTest.fetchRepositories(USER_LOGIN));
    }

    @Test
    void givenNoRepositories_shouldReturnEmpty() throws Exception {
        //given
        mockHttpClientResponseSync(200, REPOSITORIES_EMPTY_BODY);

        HttpRequest repoReq = createRepositorySearchReq();

        //when
        List<Repository> results = underTest.fetchRepositories(USER_LOGIN);

        //then
        assertEquals(0, results.size());
        verify(httpClient).send(repoReq, ofString());
        verify(httpClient, times(0)).sendAsync(any(), any());
    }

    @Test
    void givenSingleRepository_andNoBranches_shouldReturnExpected() throws Exception {
        //given
        mockHttpClientResponseSync(200, REPOSITORIES_EXAMPLE_BODY);
        mockHttpClientResponseAsync(200, BRANCHES_EMPTY_BODY);

        HttpRequest repoReq = createRepositorySearchReq();
        HttpRequest branchReq = createListBranchesReq();

        //when
        List<Repository> results = underTest.fetchRepositories(USER_LOGIN);

        //then
        assertEquals(1, results.size());

        Repository result = results.get(0);
        assertAll(
                () -> assertEquals(REPO_NAME, result.name()),
                () -> assertEquals("https://github.com/userLogin/repo-name", result.url()),
                () -> assertEquals(USER_LOGIN, result.ownerLogin()),
                () -> assertTrue(result.branches().isEmpty())
        );

        verify(httpClient).send(repoReq, ofString());
        verify(httpClient).sendAsync(branchReq, ofString());
    }

    @Test
    void givenSingleRepository_andSingleBranch_shouldReturnExpected() throws Exception {
        //given
        mockHttpClientResponseSync(200, REPOSITORIES_EXAMPLE_BODY);
        mockHttpClientResponseAsync(200, BRANCHES_EXAMPLE_BODY);

        HttpRequest repoReq = createRepositorySearchReq();
        HttpRequest branchReq = createListBranchesReq();

        //when
        List<Repository> results = underTest.fetchRepositories(USER_LOGIN);

        //then
        assertEquals(1, results.size());

        Repository result = results.get(0);
        assertAll(
                () -> assertEquals(REPO_NAME, result.name()),
                () -> assertEquals("https://github.com/userLogin/repo-name", result.url()),
                () -> assertEquals(USER_LOGIN, result.ownerLogin()),
                () -> assertEquals(1, result.branches().size())
        );

        Branch branch = result.branches().get(0);
        assertAll(
                () -> assertEquals(BRANCH_NAME, branch.name()),
                () -> assertEquals(SHA, branch.commitSha())
        );

        verify(httpClient).send(repoReq, ofString());
        verify(httpClient).sendAsync(branchReq, ofString());
    }

    private void mockHttpClientResponseSync(int statusCode, String body) throws Exception {
        given(mockResponseSync.statusCode()).willReturn(statusCode);
        given(mockResponseSync.body()).willReturn(body);

        given(httpClient.send(any(HttpRequest.class), any(ofString().getClass())))
                .willReturn(mockResponseSync);
    }

    private void mockHttpClientResponseAsync(int statusCode, String body) {
        given(mockResponseAsync.statusCode()).willReturn(statusCode);
        given(mockResponseAsync.body()).willReturn(body);

        given(httpClient.sendAsync(any(HttpRequest.class), any(ofString().getClass())))
                .willReturn(CompletableFuture.completedFuture(mockResponseAsync));
    }

    private static HttpRequest createRepositorySearchReq() throws URISyntaxException {
        return HttpRequest.newBuilder()
                .header(ACCEPT, CONTENT_TYPE)
                .uri(new URI(REPOSITORIES_URI))
                .GET()
                .build();
    }

    private static HttpRequest createListBranchesReq() throws URISyntaxException {
        return HttpRequest.newBuilder()
                .header(ACCEPT, CONTENT_TYPE)
                .uri(new URI(BRANCHES_URI))
                .GET()
                .build();
    }
}