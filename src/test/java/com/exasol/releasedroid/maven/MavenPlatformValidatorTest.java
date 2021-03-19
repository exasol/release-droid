package com.exasol.releasedroid.maven;

import static com.exasol.releasedroid.maven.MavenPlatformValidator.MAVEN_WORKFLOW_PATH;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.repository.*;
import com.exasol.releasedroid.usecases.report.Report;

@ExtendWith({ MockitoExtension.class })
class MavenPlatformValidatorTest {
    private MavenPlatformValidator platformValidator;
    @Mock
    private JavaRepository repositoryMock;
    @Mock
    private MavenPom mavenPomMock;

    @BeforeEach
    void beforeEach() {
        when(this.repositoryMock.getMavenPom()).thenReturn(this.mavenPomMock);
        this.platformValidator = new MavenPlatformValidator(this.repositoryMock);
    }

    @Test
    // [utest->dsn~validate-maven-release-workflow-exists~1]
    // [utest->dsn~validate-pom-contains-required-plugins-for-maven-release~1]
    void testValidateSuccessful() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        when(this.mavenPomMock.getPlugins()).thenReturn(Map.of( //
                "project-keeper-maven-plugin",
                MavenPlugin.builder().artifactId("project-keeper-maven-plugin").version("0.5.0").build() //
        ));
        final Report report = this.platformValidator.validate();
        assertFalse(report.hasFailures());
    }

    @Test
    // [utest->dsn~validate-maven-release-workflow-exists~1]
    void testValidateFailed() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH))
                .thenThrow(RepositoryException.class);
        final Report report = this.platformValidator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-9")), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-13")),
                () -> assertThat(report.toString(), containsString("project-keeper-maven-plugin")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "0.5.0", "0.5.1", "0.5.23", "0.6.0", "0.6.13", "1.0.0", "1.4.0" })
    void testValidateProjectKeeperVersion(final String validaVersion) {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        when(this.mavenPomMock.getPlugins()).thenReturn(Map.of( //
                "project-keeper-maven-plugin",
                MavenPlugin.builder().artifactId("project-keeper-maven-plugin").version(validaVersion).build() //
        ));
        final Report report = this.platformValidator.validate();
        assertFalse(report.hasFailures());
    }

    @ParameterizedTest
    @ValueSource(strings = { "0.1.0", "0.0.1", "0.4.2", "", "0.5" })
    void testValidateProjectKeeperVersionFailed(final String validaVersion) {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        when(this.mavenPomMock.getPlugins()).thenReturn(Map.of( //
                "project-keeper-maven-plugin",
                MavenPlugin.builder().artifactId("project-keeper-maven-plugin").version(validaVersion).build() //
        ));
        final Report report = this.platformValidator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-14")));
    }

    @Test
    void testValidateProjectKeeperMissingVersion() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        when(this.mavenPomMock.getPlugins()).thenReturn(Map.of( //
                "project-keeper-maven-plugin", MavenPlugin.builder().artifactId("project-keeper-maven-plugin").build() //
        ));
        final Report report = this.platformValidator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-14")));
    }
}