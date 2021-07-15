package com.exasol.releasedroid.adapter.maven;

import static com.exasol.releasedroid.adapter.maven.MavenPlatformValidator.MAVEN_WORKFLOW_PATH;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.adapter.repository.JavaRepository;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.report.Report;

@ExtendWith({ MockitoExtension.class })
class MavenPlatformValidatorTest {
    private MavenPlatformValidator platformValidator;
    @Mock
    private JavaRepository repositoryMock;

    @BeforeEach
    void beforeEach() {
        this.platformValidator = new MavenPlatformValidator(this.repositoryMock);
    }

    @Test
    // [utest->dsn~validate-maven-release-workflow-exists~1]
    void testValidateSuccessful() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        when(this.repositoryMock.getMavenPom())
                .thenReturn(MavenPom.builder().projectDescription("Description").projectURL("URL").build());
        final Report report = this.platformValidator.validate();
        assertFalse(report.hasFailures());
    }

    @Test
    // [utest->dsn~validate-maven-release-workflow-exists~1]
    void testValidateFailed() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH))
                .thenThrow(RepositoryException.class);
        when(this.repositoryMock.getMavenPom()).thenReturn(MavenPom.builder().build());
        final Report report = this.platformValidator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-REP-20: Project description")),
                () -> assertThat(report.toString(), containsString("E-RD-REP-20: Project URL")),
                () -> assertThat(report.toString(), containsString("E-RD-REP-19")));
    }
}