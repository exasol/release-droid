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

import com.exasol.releasedroid.usecases.repository.ReleaseConfig;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.UserInput;

@ExtendWith(MockitoExtension.class)
class ReleaseDroidTest {
    private static final String REPOSITORY_NAME = "repository";
    private static final String PLATFORM = "github";
    private static final String GOAL = "validate";
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
    void testUserInputWithoutGoal() {
        final UserInput userInput = builder().platforms(PLATFORM).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-2: Please specify a mandatory parameter 'goal'"));
    }

    @Test
    void testUserInputWithoutRepositoryName() {
        final UserInput userInput = builder().goal(GOAL).platforms(PLATFORM).build();
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
        final UserInput userInput = builder().goal("VALIDATE").platforms(PLATFORM).repositoryName(REPOSITORY_NAME)
                .branch(BRANCH).localPath(LOCAL_PATH).build();
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
        final UserInput userInput = builder().repositoryName("name").goal(GOAL).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-2: Please specify a mandatory parameter 'platforms'"));
    }

    @Test
    void testUserInputWithoutPlatforms2() {
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.empty());
        final UserInput userInput = builder().repositoryName("name").goal(GOAL).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-2: Please specify a mandatory parameter 'platforms'"));
    }

    @Test
    void testPlatformsFromConfig() {
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().releasePlatforms(List.of(PLATFORM)).build();
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        final UserInput userInput = builder().repositoryName("name").goal("RELEASE").build();
        assertThrows(NullPointerException.class, () -> this.releaseDroid.run(userInput));
    }
}