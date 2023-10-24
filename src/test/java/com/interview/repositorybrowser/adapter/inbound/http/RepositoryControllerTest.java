package com.interview.repositorybrowser.adapter.inbound.http;

import com.interview.repositorybrowser.domain.model.Branch;
import com.interview.repositorybrowser.domain.model.Repository;
import com.interview.repositorybrowser.domain.model.RepositoryType;
import com.interview.repositorybrowser.port.RepositoryClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RepositoryController.class)
class RepositoryControllerTest {
    private static final String CONTENT_TYPE = "Content-type";
    private static final String APPLICATION_JSON = "application/json";

    private static final String REPO_NAME = "repo-name";
    private static final String USER_LOGIN = "userLogin";
    private static final String BRANCH_NAME_1 = "master";
    private static final String BRANCH_NAME_2 = "feature";
    private static final String SHA = "99ee262e6df4f25e6b73594f1c43fa01410e7e";
    private static final String URL = "https://github.com/userLogin/repo-name";
    private static final String RESPONSE_JSON = "[{\"name\":\"repo-name\",\"url\":\"https://github.com/userLogin/repo-name\",\"ownerLogin\":\"userLogin\",\"branches\":[{\"name\":\"master\",\"commitSha\":\"99ee262e6df4f25e6b73594f1c43fa01410e7e\"},{\"name\":\"feature\",\"commitSha\":\"99ee262e6df4f25e6b73594f1c43fa01410e7e\"}]}]";

    @MockBean
    RepositoryClient repositoryClient;

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnClientError_whenIncorrectContentType() throws Exception {
        mockMvc.perform(get("/v1/repositories")
                        .header(CONTENT_TYPE, "application/xml")
                        .param("userLogin", USER_LOGIN)
                        .param("repositoryType", RepositoryType.GITHUB.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("{\"status\":404,\"message\":\"Unsupported media type!\"}"));
    }

    @Test
    void shouldReturnClientError_whenNoUserLogin() throws Exception {
        mockMvc.perform(get("/v1/repositories")
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .param("repositoryType", RepositoryType.GITHUB.toString()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnClientError_whenEmptyUserLogin() throws Exception {
        mockMvc.perform(get("/v1/repositories")
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .param("userLogin", "")
                        .param("repositoryType", RepositoryType.GITHUB.toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("{\"status\":400,\"message\":\"No user login provided!\"}"));
    }

    @Test
    void shouldReturnClientError_whenNoRepository() throws Exception {
        mockMvc.perform(get("/v1/repositories")
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .param("userLogin", USER_LOGIN))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnClientError_whenEmptyRepository() throws Exception {
        mockMvc.perform(get("/v1/repositories")
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .param("userLogin", USER_LOGIN)
                        .param("repositoryType", ""))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnEmpty_whenNoResultsGiven() throws Exception {
        mockMvc.perform(get("/v1/repositories")
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .param("userLogin", USER_LOGIN)
                        .param("repositoryType", RepositoryType.GITHUB.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string("[]"));
    }

    @Test
    void shouldReturnExpected() throws Exception {
        given(repositoryClient.fetchRepositories(USER_LOGIN))
                .willReturn(createResultList());

        mockMvc.perform(get("/v1/repositories")
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .param("userLogin", USER_LOGIN)
                        .param("repositoryType", RepositoryType.GITHUB.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(RESPONSE_JSON));
    }

    private static List<Repository> createResultList() {
        List<Branch> branches = List.of(new Branch(BRANCH_NAME_1, SHA), new Branch(BRANCH_NAME_2, SHA));
        Repository repository = new Repository(REPO_NAME, URL, USER_LOGIN, branches);
        return List.of(repository);
    }

}