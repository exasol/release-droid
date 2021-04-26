package com.exasol.releasedroid.adapter.maven;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.adapter.github.GitHubException;
import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.release.ReleaseMaker;
import com.exasol.releasedroid.usecases.repository.Repository;

@ExtendWith(MockitoExtension.class)
class MavenReleaseMakerTest {
    @Mock
    private GitHubGateway githubGatewayMock;
    @Mock
    private Repository repositoryMock;
    private ReleaseMaker releaseMaker;

    @BeforeEach
    void beforeEach() {
        when(this.repositoryMock.getName()).thenReturn("name");
        this.releaseMaker = new MavenReleaseMaker(this.githubGatewayMock);
    }

    @Test
    // [utest->dsn~create-new-maven-release~1]
    void testMakeRelease() {
        assertAll(() -> assertDoesNotThrow(() -> this.releaseMaker.makeRelease(this.repositoryMock)),
                () -> verify(this.githubGatewayMock, times(1)).executeWorkflow("name",
                        "release_droid_release_on_maven_central.yml"));
    }

    @Test
    // [utest->dsn~create-new-maven-release~1]
    void testMakeReleaseFails() throws GitHubException {
        doThrow(GitHubException.class).when(this.githubGatewayMock).executeWorkflow("name",
                "release_droid_release_on_maven_central.yml");
        assertAll(() -> assertThrows(ReleaseException.class, () -> this.releaseMaker.makeRelease(this.repositoryMock)),
                () -> verify(this.githubGatewayMock, times(1)).executeWorkflow("name",
                        "release_droid_release_on_maven_central.yml"));
    }
}