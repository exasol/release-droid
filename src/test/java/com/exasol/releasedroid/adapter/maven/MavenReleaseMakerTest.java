package com.exasol.releasedroid.adapter.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.adapter.github.GitHubException;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.maven.MavenPom.Builder;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.repository.Repository;

@ExtendWith(MockitoExtension.class)
class MavenReleaseMakerTest {
    private static final String REPOSITORY_NAME = "name";
    private static final String MAVEN_ARTIFACT_ID = "maven-artifact-id";
    private static final String MAVEN_GROUP_ID = "maven.group.id";
    private static final String MAVEN_VERSION = "maven version";

    @Mock
    private GitHubGateway githubGatewayMock;
    @Mock
    private MavenRepository repositoryMock;
    private ReleaseMaker releaseMaker;
    private Builder mavenPomBuilder;

    @BeforeEach
    void beforeEach() {
        lenient().when(this.repositoryMock.getName()).thenReturn(REPOSITORY_NAME);
        this.releaseMaker = new MavenReleaseMaker(this.githubGatewayMock);
        this.mavenPomBuilder = MavenPom.builder().artifactId(MAVEN_ARTIFACT_ID).groupId(MAVEN_GROUP_ID)
                .version(MAVEN_VERSION);
    }

    @Test
    // [utest->dsn~create-new-maven-release~1]
    void testMakeRelease() {
        simulateMavenPom();
        assertAll(() -> assertDoesNotThrow(() -> this.releaseMaker.makeRelease(this.repositoryMock)),
                () -> verify(this.githubGatewayMock, times(1)).executeWorkflow(REPOSITORY_NAME,
                        "release_droid_release_on_maven_central.yml"));
    }

    <T extends Throwable> void assertThrows(final Class<T> expectedType, final Executable executable,
            final Matcher<String> messageMatcher) {
        final T exception = Assertions.assertThrows(expectedType, executable);
        assertThat(exception.getMessage(), messageMatcher);
    }

    @Test
    void testMakeReleaseForNonJavaProjectFails() {
        final Repository nonJavaRepository = mock(Repository.class);
        assertAll(
                () -> assertThrows(ReleaseException.class, () -> this.releaseMaker.makeRelease(nonJavaRepository),
                        equalTo("E-RD-REP-29: Cannot make a Maven release for repository of type '"
                                + nonJavaRepository.getClass().getName() + "'")), //
                () -> verify(this.githubGatewayMock, never()).executeWorkflow(any(), any()));
    }

    @Test
    void testMakeReleaseWithoutMavenPomFails() {
        when(this.repositoryMock.getMavenPom()).thenReturn(null);
        assertAll(
                () -> assertThrows(ReleaseException.class, () -> this.releaseMaker.makeRelease(this.repositoryMock),
                        equalTo("E-RD-REP-30: Repository '" + REPOSITORY_NAME + "' does not have Maven POM file")), //
                () -> verify(this.githubGatewayMock, never()).executeWorkflow(any(), any()));
    }

    @Test
    // [utest->dsn~create-new-maven-release~1]
    void testMakeReleaseFails() throws GitHubException {
        simulateMavenPom();
        doThrow(GitHubException.class).when(this.githubGatewayMock).executeWorkflow(REPOSITORY_NAME,
                "release_droid_release_on_maven_central.yml");
        assertAll(
                () -> Assertions.assertThrows(ReleaseException.class,
                        () -> this.releaseMaker.makeRelease(this.repositoryMock)),
                () -> verify(this.githubGatewayMock, times(1)).executeWorkflow(REPOSITORY_NAME,
                        "release_droid_release_on_maven_central.yml"));
    }

    @ParameterizedTest
    @CsvSource({ //
            "artifact-id, com.exasol, version,        https://repo1.maven.org/maven2/com/exasol/artifact-id/version/",
            "art,         com.exasol, version,        https://repo1.maven.org/maven2/com/exasol/art/version/",
            "artifact-id, comexasol,  version,        https://repo1.maven.org/maven2/comexasol/artifact-id/version/",
            "artifact-id, a.b.c.d.e,  version,        https://repo1.maven.org/maven2/a/b/c/d/e/artifact-id/version/",
            "artifact-id, com.exasol, 1.2.3,          https://repo1.maven.org/maven2/com/exasol/artifact-id/1.2.3/",
            "artifact-id, com.exasol, 1.2.3-SNAPSHOT, https://repo1.maven.org/maven2/com/exasol/artifact-id/1.2.3-SNAPSHOT/" })
    void testReleaseUrl(final String artifactId, final String groupId, final String version,
            final String expectedReleaseUrl) {
        this.mavenPomBuilder.artifactId(artifactId).groupId(groupId).version(version);
        simulateMavenPom();
        final String releaseUrl = this.releaseMaker.makeRelease(this.repositoryMock);
        assertThat(releaseUrl, equalTo(expectedReleaseUrl));
    }

    private void simulateMavenPom() {
        when(this.repositoryMock.getMavenPom()).thenReturn(this.mavenPomBuilder.build());
    }
}