package com.exasol.releasedroid.usecases.request;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.repository.Repository;

@ExtendWith(MockitoExtension.class)
class ReleasePlatformsTest {

    private static final List<PlatformName> PLATFORMS = List.of( //
            PlatformName.GITHUB, //
            PlatformName.JIRA, //
            PlatformName.MAVEN, //
            PlatformName.COMMUNITY);
    private static final List<PlatformName> VALID = List.of( //
            PlatformName.GITHUB, //
            PlatformName.MAVEN);

    @Mock
    private Repository repositoryMock;
    @Mock
    private UserInput userInputMock;

    @Test
    void noPlatform() {
        assertThrows(IllegalArgumentException.class,
                () -> ReleasePlatforms.from(this.userInputMock, this.repositoryMock));
    }

    @Test
    void deprecated_Removed() {
        final ReleasePlatforms platforms = run(Goal.RELEASE, false);
        assertThat(platforms.list(), equalTo(VALID));
    }

    @Test
    void unreleased() {
        final ReleasePlatforms platforms = run(Goal.RELEASE, false).remaining(List.of(PlatformName.MAVEN), p -> {
        });
        assertThat(platforms.list(), equalTo(List.of(PlatformName.GITHUB)));
    }

    @Test
    void validateSkipRelease() {
        final ReleasePlatforms platforms = run(Goal.RELEASE, true);
        assertThat(platforms.skipValidationOn(), equalTo(new HashSet<>(PLATFORMS)));
    }

    @Test
    void validateSkipNone() {
        final ReleasePlatforms platforms = run(Goal.RELEASE, false);
        assertThat(platforms.skipValidationOn(), equalTo(Collections.emptySet()));
    }

    @Test
    void validateSkipValidate() {
        final ReleasePlatforms platforms = run(Goal.VALIDATE, false);
        assertThat(platforms.skipValidationOn(), equalTo(Set.of(PlatformName.JIRA)));
    }

    private ReleasePlatforms run(final Goal goal, final boolean skip) {
        when(this.userInputMock.getPlatformNames()).thenReturn(PLATFORMS);
        when(this.userInputMock.hasPlatforms()).thenReturn(true);
        when(this.userInputMock.getGoal()).thenReturn(goal);
        when(this.userInputMock.skipValidation()).thenReturn(skip);
        return ReleasePlatforms.from(this.userInputMock, this.repositoryMock);
    }
}
