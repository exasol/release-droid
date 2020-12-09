package com.exasol.releasedroid.maven;

import static com.exasol.releasedroid.maven.MavenPlatformValidator.MAVEN_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.maven.model.PluginExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.repository.RepositoryException;
import com.exasol.releasedroid.repository.maven.*;
import com.exasol.releasedroid.usecases.report.Report;

@ExtendWith({ MockitoExtension.class })
class MavenPlatformValidatorTest {
    private final MavenPlatformValidator platformValidator = new MavenPlatformValidator();
    @Mock
    private MavenRepository repositoryMock;
    @Mock
    private MavenPom mavenPomMock;
    @Mock
    private PluginExecution pluginExecutionMock;
    @Mock
    private Object configurationsMock;

    @BeforeEach
    void beforeEach() {
        when(this.repositoryMock.getMavenPom()).thenReturn(this.mavenPomMock);
    }

    @Test
    // [utest->dsn~validate-maven-release-workflow-exists~1]
    void testValidateSuccessful() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        when(this.pluginExecutionMock.getId()).thenReturn("sign-artifacts");
        when(this.pluginExecutionMock.getConfiguration()).thenReturn(this.configurationsMock);
        when(this.configurationsMock.toString()).thenReturn("--pinentry-mode");
        when(this.mavenPomMock.getPlugins()).thenReturn(List.of( //
                MavenPlugin.builder().artifactId("nexus-staging-maven-plugin").build(), //
                MavenPlugin.builder().artifactId("maven-source-plugin").build(), //
                MavenPlugin.builder().artifactId("maven-gpg-plugin").executions(List.of(this.pluginExecutionMock))
                        .build(), //
                MavenPlugin.builder().artifactId("maven-javadoc-plugin").build(), //
                MavenPlugin.builder().artifactId("maven-deploy-plugin").build()//
        ));
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-maven-release-workflow-exists~1]
    void testValidateFails() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH))
                .thenThrow(RepositoryException.class);
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertAll(() -> assertTrue(report.hasFailures()),
                () -> assertTrue(report.getResults().stream().anyMatch(r -> r.toString().contains("E-RR-VAL-13"))),
                () -> assertTrue(report.getResults().stream()
                        .anyMatch(r -> r.toString().contains("nexus-staging-maven-plugin"))),
                () -> assertTrue(
                        report.getResults().stream().anyMatch(r -> r.toString().contains("maven-source-plugin"))),
                () -> assertTrue(report.getResults().stream().anyMatch(r -> r.toString().contains("maven-gpg-plugin"))),
                () -> assertTrue(
                        report.getResults().stream().anyMatch(r -> r.toString().contains("maven-javadoc-plugin"))));
    }

    @Test
    // [utest->dsn~validate-pom-contains-required-plugins-for-maven-release~1]
    void testValidatePGpgPluginMissingExecutions() {
        when(this.mavenPomMock.getPlugins())
                .thenReturn(List.of(MavenPlugin.builder().artifactId("maven-gpg-plugin").build()));
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertTrue(report.getResults().stream().anyMatch(r -> r.toString().contains("E-RR-VAL-14"))));
    }

    @Test
    // [utest->dsn~validate-pom-contains-required-plugins-for-maven-release~1]
    void testValidateGpgPluginMissingRequiredExecution() {
        when(this.mavenPomMock.getPlugins()).thenReturn(List.of(MavenPlugin.builder().artifactId("maven-gpg-plugin")
                .executions(List.of(this.pluginExecutionMock)).build()));
        when(this.pluginExecutionMock.getId()).thenReturn("some-id");
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertTrue(report.getResults().stream().anyMatch(r -> r.toString().contains("E-RR-VAL-15"))));
    }

    @Test
    // [utest->dsn~validate-pom-contains-required-plugins-for-maven-release~1]
    void testValidatePGpgPluginMissingRequiredExecutionConfiguration() {
        when(this.mavenPomMock.getPlugins()).thenReturn(List.of(MavenPlugin.builder().artifactId("maven-gpg-plugin")
                .executions(List.of(this.pluginExecutionMock)).build()));
        when(this.pluginExecutionMock.getId()).thenReturn("sign-artifacts");
        when(this.pluginExecutionMock.getConfiguration()).thenReturn(this.configurationsMock);
        when(this.configurationsMock.toString()).thenReturn("text");
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertTrue(report.getResults().stream().anyMatch(r -> r.toString().contains("E-RR-VAL-16"))));
    }
}
