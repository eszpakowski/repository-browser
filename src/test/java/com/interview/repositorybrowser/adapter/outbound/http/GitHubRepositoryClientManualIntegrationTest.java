package com.interview.repositorybrowser.adapter.outbound.http;

import com.interview.repositorybrowser.domain.exception.UserNotFoundException;
import com.interview.repositorybrowser.domain.model.Repository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("For manual testing only")
@SpringBootTest
class GitHubRepositoryClientManualIntegrationTest {
    @Autowired
    GitHubRepositoryClient underTest;

    @Test
    void shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class,
                () -> underTest.fetchRepositories("UnexistingUser123X"));
    }

    @Test
    void shouldFetchNonEmpty() {
        List<Repository> repositories =
                underTest.fetchRepositories("eszpakowski");

        assertFalse(repositories.isEmpty());
    }
}