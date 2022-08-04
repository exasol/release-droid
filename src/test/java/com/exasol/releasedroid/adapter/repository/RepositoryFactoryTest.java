package com.exasol.releasedroid.adapter.repository;

import static com.exasol.releasedroid.usecases.request.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.adapter.github.GitHubException;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.UserInput;

@ExtendWith(MockitoExtension.class)
class RepositoryFactoryTest {
    @Mock
    private GitHubGateway githubGateway;
    private RepositoryGateway repositoryGateway;

    @BeforeEach
    void beforeEach() {
        this.repositoryGateway = new RepositoryFactory(this.githubGateway);
    }

    @Test
    void getJavaRepository() throws GitHubException {
        verifyRepository("java", JavaRepository.class);
    }

    @Test
    void getScalaRepository() throws GitHubException {
        verifyRepository("scala", ScalaRepository.class);
    }

    void verifyRepository(final String language, final Class<?> expectedClass) throws GitHubException {
        verifyRepositoryFromUserInput(language, expectedClass);
        verifyRepositoryFromReleaseConfig(language, expectedClass);
        verifyRepositoryFromPrimaryLanguage(language, expectedClass);
    }

    void verifyRepositoryFromUserInput(final String language, final Class<?> expectedClass) {
        final UserInput userInput = builder().repositoryName("my-repo").platforms("GitHub").goal("validate")
                .language(language).build();
        assertThat(this.repositoryGateway.getRepository(userInput), instanceOf(expectedClass));
    }

    void verifyRepositoryFromReleaseConfig(final String language, final Class<?> expectedClass) throws GitHubException {
        simulateReleaseConfig(this.githubGateway, "null/my-repo", "language: " + language);
        final UserInput userInput = builder().repositoryName("my-repo").platforms("GitHub").goal("validate").build();
        assertThat(this.repositoryGateway.getRepository(userInput), instanceOf(expectedClass));
    }

    private void simulateReleaseConfig(final GitHubGateway gateway, final String repo, final String content)
            throws GitHubException {
        when(gateway.getFileContent(eq(repo), eq(null), any()))
                .thenReturn(new ByteArrayInputStream(content.getBytes()));
    }

    void verifyRepositoryFromPrimaryLanguage(final String language, final Class<?> expectedClass)
            throws GitHubException {
        simulateReleaseConfig(this.githubGateway, "exasol/my-repo", "");
        when(this.githubGateway.getRepositoryPrimaryLanguage("exasol/my-repo")).thenReturn(language);
        final UserInput userInput = builder().owner("exasol").repositoryName("my-repo").platforms("GitHub")
                .goal("validate").build();
        assertThat(this.repositoryGateway.getRepository(userInput), instanceOf(expectedClass));
    }

    @Test
    void tryCreatingUnsupportedRepository() throws GitHubException {
        when(this.githubGateway.getFileContent(any(), any(), any())).thenThrow(GitHubException.class);
        when(this.githubGateway.getRepositoryPrimaryLanguage("exasol/my-repo")).thenReturn("python");
        final UserInput userInput = builder().owner("exasol").repositoryName("my-repo").platforms("GitHub")
                .goal("validate").build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.repositoryGateway.getRepository(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-8"));
    }
}