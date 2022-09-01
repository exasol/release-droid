package com.exasol.releasedroid.main;

import static com.exasol.releasedroid.usecases.request.UserInput.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ReleaseDroid releaseDroid;

    @BeforeEach
    void beforeEach() {
        this.releaseDroid = new ReleaseDroid(this.repositoryGatewayMock, null, null, null);
    }

    @Test
    void testUserInputWithoutRepositoryName() {
        final UserInput userInput = builder().platforms(PLATFORM).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(),
                containsString("E-RD-2: Please specify a mandatory parameter 'repository name'"));
    }

    @Test
    void testUserInputWithReleaseAndBranch() {
        final UserInput userInput = builder().goal("RELEASE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .branch(BRANCH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-1"));
    }

    @Test
    void testUserInputWithLocalPathAndBranch() {
        final UserInput userInput = builder().platforms(PLATFORM).repositoryName(REPOSITORY_NAME).branch(BRANCH)
                .localPath(LOCAL_PATH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-6"));
    }

    @Test
    void testUserInputWithLocalPathAndGoalRelease() {
        final UserInput userInput = builder().goal("RELEASE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .localPath(LOCAL_PATH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-6"));
    }

    @Test
    void testUserInputWithoutPlatforms() {
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().build();
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        final UserInput userInput = builder().repositoryName("name").build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(),
                containsString("E-RD-20: Platform specified neither on commandline nor in configuration file"));
    }

    @Test
    void testUserInputWithoutPlatforms2() {
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.empty());
        final UserInput userInput = builder().repositoryName("name").build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(),
                containsString("E-RD-20: Platform specified neither on commandline nor in configuration file"));
    }

    @Test
    void testPlatformsFromConfig() {
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().releasePlatforms(List.of(PLATFORM)).build();
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        final UserInput userInput = builder().repositoryName("name").goal("RELEASE").build();
        assertThrows(NullPointerException.class, () -> this.releaseDroid.run(userInput));
    }

    @Test
    void deprecatedPlatforms() {
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().releasePlatforms(List.of("jira")).build();
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        final UserInput userInput = builder().repositoryName("name").goal("RELEASE").build();
        assertThrows(NullPointerException.class, () -> this.releaseDroid.run(userInput));
    }

    @Test
    void testUserInputWithSkippingValidation() {
        final UserInput userInput = builder().repositoryName("name").platforms("github").skipValidation(true)
                .goal("validate").build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-15"));
    }
}