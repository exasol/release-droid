package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.request.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.adapter.RepositoryFactory;
import com.exasol.releasedroid.adapter.github.GitHubException;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.java.JavaRepository;
import com.exasol.releasedroid.adapter.scala.ScalaRepository;
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
    void getJavaRepository() {
        final UserInput userInput = builder().repositoryName("my-repo").platforms("GitHub").goal("validate")
                .language("java").build();
        assertThat(this.repositoryGateway.getRepository(userInput), instanceOf(JavaRepository.class));
    }

    @Test
    void getJavaRepositoryWithAutoDetectedLanguage() throws GitHubException {
        when(this.githubGateway.getRepositoryPrimaryLanguage("exasol/my-repo")).thenReturn("java");
        final UserInput userInput = builder().repositoryName("my-repo").platforms("GitHub").goal("validate").build();
        assertThat(this.repositoryGateway.getRepository(userInput), instanceOf(JavaRepository.class));
    }

    @Test
    void getScalaRepository() {
        final UserInput userInput = builder().repositoryName("my-repo").platforms("GitHub").goal("validate")
                .language("scala").build();
        assertThat(this.repositoryGateway.getRepository(userInput), instanceOf(ScalaRepository.class));
    }

    @Test
    void getScalaRepositoryWithAutoDetectedLanguage() throws GitHubException {
        when(this.githubGateway.getRepositoryPrimaryLanguage("exasol/my-repo")).thenReturn("scala");
        final UserInput userInput = builder().repositoryName("my-repo").platforms("GitHub").goal("validate").build();
        assertThat(this.repositoryGateway.getRepository(userInput), instanceOf(ScalaRepository.class));
    }

    @Test
    void tryCreatingUnsupportedRepository() throws GitHubException {
        when(this.githubGateway.getRepositoryPrimaryLanguage("exasol/my-repo")).thenReturn("python");
        final UserInput userInput = builder().repositoryName("my-repo").platforms("GitHub").goal("validate").build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.repositoryGateway.getRepository(userInput));
        assertThat(exception.getMessage(), containsString("E-RR-8"));
    }
}