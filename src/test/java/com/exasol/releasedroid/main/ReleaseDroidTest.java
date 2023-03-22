package com.exasol.releasedroid.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.UseCase;
import com.exasol.releasedroid.usecases.release.ReleaseState;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.repository.*;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.request.UserInput;

@ExtendWith(MockitoExtension.class)
class ReleaseDroidTest {
    private static final String REPOSITORY_NAME = "repository";
    private static final String REPOSITORY_VERSION = "1.2.3";
    private static final String PLATFORM = "github";
    private static final String BRANCH = "branch";
    private static final String LOCAL_PATH = "some/path";
    @Mock
    private RepositoryGateway repositoryGatewayMock;
    @Mock
    private Repository repositoryMock;
    @Mock
    private ReleaseState releaseStateMock;
    @Mock
    UseCase validationUseCaseMock;
    @Mock
    UseCase releaseUseCaseMock;
    @Mock
    ReleaseDroidResponseConsumer responseConsumerMock;
    private ReleaseDroid releaseDroid;

    @BeforeEach
    void beforeEach() {
        this.releaseDroid = ReleaseDroid.builder() //
                .repositoryGateway(this.repositoryGatewayMock) //
                .releaseState(this.releaseStateMock) //
                .validateUseCase(this.validationUseCaseMock) //
                .releaseUseCase(this.releaseUseCaseMock) //
                .loggerResponseConsumer(this.responseConsumerMock) //
                .diskWriterResponseConsumer(this.responseConsumerMock) //
                .build();
    }

    @Test
    void userInputWithoutRepositoryName() {
        final UserInput userInput = UserInput.builder().platforms(PLATFORM).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(),
                containsString("E-RD-2: Please specify mandatory parameter 'repository name'"));
    }

    @Test
    void userInputWithReleaseAndBranch() {
        final UserInput userInput = userInput("RELEASE").branch(BRANCH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-1"));
    }

    @Test
    void userInputWithLocalPathAndBranch() {
        final UserInput userInput = userInput().branch(BRANCH).localPath(LOCAL_PATH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-6"));
    }

    @Test
    void userInputWithLocalPathAndGoalRelease() {
        final UserInput userInput = userInput("RELEASE").localPath(LOCAL_PATH).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-6"));
    }

    @Test
    void userInputWithoutPlatforms() {
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().build();
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        final UserInput userInput = UserInput.builder().repositoryName("name").build();
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
        mockUseCase(this.releaseUseCaseMock);
        final UserInput userInput = UserInput.builder().repositoryName(REPOSITORY_NAME).goal("RELEASE").build();
        assertThrows(UseCaseException.class, () -> this.releaseDroid.run(userInput));
    }

    @Test
    void deprecatedPlatforms() {
        final ReleaseConfig releaseConfig = ReleaseConfig.builder().releasePlatforms(List.of("jira")).build();
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(releaseConfig));
        final UserInput userInput = UserInput.builder().repositoryName(REPOSITORY_NAME).goal("RELEASE").build();
        this.releaseDroid.run(userInput);
        verifyNoInteractions(this.validationUseCaseMock);
        verifyNoInteractions(this.releaseUseCaseMock);
    }

    @Test
    void userInputWithSkippingValidation() {
        final UserInput userInput = userInput("validate").skipValidation(true).build();
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.releaseDroid.run(userInput));
        assertThat(exception.getMessage(), containsString("E-RD-15"));
    }

    @ParameterizedTest
    @CsvSource(value = { "release, false, true", //
            "release, true, false", //
            "validate, false, true", })
    void validation(final String goal, final boolean skipValidation, final boolean expectValidation) {
        final UserInput userInput = userInput(goal).skipValidation(skipValidation).build();
        mockRepositoryAndState();
        this.releaseDroid.run(userInput);
        if (expectValidation) {
            verify(this.validationUseCaseMock).apply(any(), any());
        } else {
            verifyNoMoreInteractions(this.validationUseCaseMock);
        }
        if (goal.equals("release")) {
            verify(this.releaseUseCaseMock).apply(any(), any());
        } else {
            verifyNoMoreInteractions(this.releaseUseCaseMock);
        }
    }

    @Test
    void failedValidationSkipsReleases() {
        simulateFailure(this.validationUseCaseMock, ValidationReport.create(PlatformName.GITHUB));
        mockRepositoryAndState();
        this.releaseDroid.run(userInput("release").build());
        verifyNoMoreInteractions(this.releaseUseCaseMock);
    }

    @Test
    void testSkipAlreadyReleased() {
        mockRepositoryAndState();
        when(this.repositoryMock.getName()).thenReturn(REPOSITORY_NAME);
        when(this.repositoryMock.getVersion()).thenReturn(REPOSITORY_VERSION);
        when(this.releaseStateMock.getAlreadyReleased(REPOSITORY_NAME, REPOSITORY_VERSION))
                .thenReturn(Set.of(PlatformName.GITHUB));
        this.releaseDroid.run(userInput("release").build());
        verifyNoInteractions(this.validationUseCaseMock);
        verifyNoInteractions(this.releaseUseCaseMock);
    }

    private void mockRepositoryAndState() {
        when(this.repositoryGatewayMock.getRepository(any())).thenReturn(this.repositoryMock);
        when(this.releaseStateMock.getAlreadyReleased(any(), any())).thenReturn(Set.of());
    }

    private void simulateFailure(final UseCase useCase, final Report report) {
        report.addFailedResult("failure");
        when(useCase.apply(any(), any())).thenReturn(List.of(report));
    }

    private void mockUseCase(final UseCase useCase) {
        when(useCase.apply(eq(this.repositoryMock), any())).thenThrow(UseCaseException.class);
    }

    private static class UseCaseException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private UserInput.Builder userInput(final String goal) {
        return userInput().goal(goal);
    }

    private UserInput.Builder userInput() {
        return UserInput.builder().repositoryName(REPOSITORY_NAME).platforms(PLATFORM);
    }
}