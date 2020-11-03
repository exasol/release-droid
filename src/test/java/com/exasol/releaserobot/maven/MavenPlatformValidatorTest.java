package com.exasol.releaserobot.maven;

import static com.exasol.releaserobot.maven.MavenPlatformValidator.MAVEN_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.repository.GitRepositoryException;
import com.exasol.releaserobot.repository.maven.JavaMavenGitBranchContent;
import com.exasol.releaserobot.usecases.Report;
import com.exasol.releaserobot.usecases.validate.PlatformValidator;

class MavenPlatformValidatorTest {
    @Test
    void testValidateWorkflow() {
        final JavaMavenGitBranchContent branchContentMock = Mockito.mock(JavaMavenGitBranchContent.class);
        when(branchContentMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        final PlatformValidator platformValidator = new MavenPlatformValidator(branchContentMock);
        final Report report = platformValidator.validate();
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    void testValidateWorkflowFails() {
        final JavaMavenGitBranchContent branchContentMock = Mockito.mock(JavaMavenGitBranchContent.class);
        when(branchContentMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH))
                .thenThrow(GitRepositoryException.class);
        final PlatformValidator platformValidator = new MavenPlatformValidator(branchContentMock);
        final Report report = platformValidator.validate();
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-9")));
    }
}