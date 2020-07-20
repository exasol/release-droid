package com.exasol.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.mockito.Mockito;

class JavaGitHubRepositoryTest {
    @Test
    void testGetVersionWithCaching() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final String version = "<version>1.0.0</version>";
        when(contentMock.getContent()).thenReturn(version);
        when(ghRepositoryMock.getFileContent(anyString())).thenReturn(contentMock);
        final GitHubRepository repository = new JavaGitHubRepository(ghRepositoryMock);
        assertAll(() -> assertThat(repository.getVersion(), equalTo("1.0.0")),
                () -> assertThat(repository.getVersion(), equalTo("1.0.0")),
                () -> verify(ghRepositoryMock, times(1)).getFileContent(anyString()));
    }

    @Test
    void testGetVersionInvalidPom() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final String version = "nothing here";
        when(contentMock.getContent()).thenReturn(version);
        when(ghRepositoryMock.getFileContent(anyString())).thenReturn(contentMock);
        final GitHubRepository repository = new JavaGitHubRepository(ghRepositoryMock);
        assertThrows(GitHubException.class, repository::getVersion);
    }
}