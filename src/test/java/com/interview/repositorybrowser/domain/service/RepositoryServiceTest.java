package com.interview.repositorybrowser.domain.service;

import com.interview.repositorybrowser.domain.model.RepositoryType;
import com.interview.repositorybrowser.port.RepositoryClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {
    private static final String USER_LOGIN = "userLogin";

    @Mock
    RepositoryClient repositoryClient;
    @InjectMocks
    RepositoryService underTest;

    @Test
    void shouldThrow_whenUserLoginIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> underTest.listRepositories(null, RepositoryType.GITHUB));
    }

    @Test
    void shouldThrow_whenRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> underTest.listRepositories(USER_LOGIN, null));
    }

    @Test
    void shouldCallRepositoryClient() {
        underTest.listRepositories(USER_LOGIN, RepositoryType.GITHUB);

        verify(repositoryClient).fetchRepositories(USER_LOGIN);
    }
}