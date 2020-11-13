package com.exasol.releaserobot.maven;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GithubGateway;
import com.exasol.releaserobot.usecases.ReleaseException;
import com.exasol.releaserobot.usecases.Repository;
import com.exasol.releaserobot.usecases.release.ReleaseMaker;

@ExtendWith(MockitoExtension.class)
class MavenReleaseMakerTest {
    @Mock
    private GithubGateway githubGatewayMock;
    @Mock
    private Repository repositoryMock;
    private ReleaseMaker releaseMaker;

    @BeforeEach
    void beforeEach() {
        when(this.repositoryMock.getBranchName()).thenReturn("main");
        when(this.repositoryMock.getFullName()).thenReturn("name");
        this.releaseMaker = new MavenReleaseMaker(this.githubGatewayMock);
    }

    @Test
    // [utest->dsn~create-new-maven-release~1]
    void testMakeRelease() {
        assertAll(() -> assertDoesNotThrow(() -> this.releaseMaker.makeRelease(this.repositoryMock)),
                () -> verify(this.githubGatewayMock, times(1)).executeWorkflow("name", "maven_central_release.yml",
                        "{\"ref\":\"main\"}"));
    }

    @Test
    // [utest->dsn~create-new-maven-release~1]
    void testMakeReleaseFails() throws GitHubException {
        doThrow(GitHubException.class).when(this.githubGatewayMock).executeWorkflow("name", "maven_central_release.yml",
                "{\"ref\":\"main\"}");
        assertAll(() -> assertThrows(ReleaseException.class, () -> this.releaseMaker.makeRelease(this.repositoryMock)),
                () -> verify(this.githubGatewayMock, times(1)).executeWorkflow("name", "maven_central_release.yml",
                        "{\"ref\":\"main\"}"));
    }
}