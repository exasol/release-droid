package com.exasol.releaserobot.maven;

import static com.exasol.releaserobot.maven.MavenPlatformValidator.MAVEN_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releaserobot.repository.GitRepositoryException;
import com.exasol.releaserobot.repository.Repository;
import com.exasol.releaserobot.repository.maven.JavaMavenGitBranch;
import com.exasol.releaserobot.usecases.Report;
import com.exasol.releaserobot.usecases.validate.PlatformValidator;

@ExtendWith({ MockitoExtension.class })
class MavenPlatformValidatorTest {
    private final PlatformValidator platformValidator = new MavenPlatformValidator();
    @Mock
    private Repository repositoryMock;
    @Mock
    private JavaMavenGitBranch branchMock;

    @BeforeEach
    void beforeEach() {
        when(this.repositoryMock.getBranch()).thenReturn(this.branchMock);
    }

    @Test
    void testValidateWorkflow() {
        when(this.branchMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    void testValidateWorkflowFails() {
        when(this.branchMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenThrow(GitRepositoryException.class);
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-9")));
    }
}