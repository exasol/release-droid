package com.exasol.releasedroid.main;

import static com.exasol.releasedroid.usecases.request.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.UseCase;
import com.exasol.releasedroid.usecases.repository.*;
import com.exasol.releasedroid.usecases.request.UserInput;

@ExtendWith(MockitoExtension.class)
class ReleaseDroidTest {
    private static final String REPOSITORY_NAME = "repository";
    private static final String PLATFORM = "github";
    private static final String BRANCH = "branch";
    private static final String LOCAL_PATH = "some/path";
    @Mock
    private RepositoryGateway repositoryGatewayMock;
    @Mock
    private Repository repositoryMock;
    @Mock
    UseCase validationUseCaseMock;
    @Mock
    UseCase releaseUseCaseMock;
    private ReleaseDroid releaseDroid;

    @BeforeEach
    void beforeEach() {
        this.releaseDroid = new ReleaseDroid(this.repositoryGatewayMock, this.validationUseCaseMock,
                this.releaseUseCaseMock, null);
    }

    @Test
    void userInputWithoutRepositoryName() {
        final UserInput userInput = builder().platforms(PLATFORM).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(),
                containsString("E-RD-2: Please specify a mandatory parameter 'repository name'"));
    }

    @Test
    void userInputWithReleaseAndBranch() {
        final UserInput userInput = builder().goal("RELEASE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .branch(BRANCH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-1"));
    }

    @Test
    void userInputWithLocalPathAndBranch() {
        final UserInput userInput = builder().platforms(PLATFORM).repositoryName(REPOSITORY_NAME).branch(BRANCH)
                .localPath(LOCAL_PATH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-6"));
    }

    @Test
    void userInputWithLocalPathAndGoalRelease() {
        final UserInput userInput = builder().goal("RELEASE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .localPath(LOCAL_PATH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-6"));
    }

    @Test
    void userInputWithoutPlatforms() {
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().build();
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        final UserInput userInput = builder().repositoryName("name").build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-20: No release platform specified." //
                + " Please specify at least one release platform either on command line"
                + " or with key 'release-platforms' in file 'release_config.yml'"));
    }

    @Test
    void platformsFromConfig() {
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().releasePlatforms(List.of(PLATFORM)).build();
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        mockUseVCase(this.releaseUseCaseMock);
        final UserInput userInput = builder().repositoryName("name").goal("RELEASE").build();
        assertThrows(UseCaseException.class, () -> this.releaseDroid.run(userInput));
    }

    @Test
    void deprecatedPlatforms() {
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().releasePlatforms(List.of("jira")).build();
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        mockUseVCase(this.releaseUseCaseMock);
        final UserInput userInput = builder().repositoryName("name").goal("RELEASE").build();
        assertThrows(UseCaseException.class, () -> this.releaseDroid.run(userInput));
    }

    @Test
    void userInputWithSkippingValidation() {
        final UserInput userInput = builder().repositoryName("name").platforms("github").skipValidation(true)
                .goal("validate").build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-15"));
    }

    private void mockUseVCase(final UseCase useCase) {
        when(useCase.apply(eq(this.repositoryMock), any())).thenThrow(UseCaseException.class);
    }

    private static class UseCaseException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}