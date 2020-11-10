package com.exasol.releaserobot.maven;

import static com.exasol.releaserobot.maven.MavenPlatformValidator.MAVEN_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.List;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releaserobot.repository.GitRepositoryException;
import com.exasol.releaserobot.repository.maven.*;
import com.exasol.releaserobot.usecases.Report;

@ExtendWith({ MockitoExtension.class })
class MavenPlatformValidatorTest {
    private final MavenPlatformValidator platformValidator = new MavenPlatformValidator();
    @Mock
    private MavenRepository repositoryMock;
    @Mock
    private MavenPom mavenPomMock;
    @Mock
    private Xpp3Dom xpp3DomMock;

    @BeforeEach
    void beforeEach() {
        when(this.repositoryMock.getMavenPom()).thenReturn(this.mavenPomMock);
    }

    @Test
    void testValidateSuccessful() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH)).thenReturn("I exist");
        when(this.mavenPomMock.getPlugins()).thenReturn(List.of( //
                MavenPlugin.builder().artifactId("nexus-staging-maven-plugin").build(), //
                MavenPlugin.builder().artifactId("maven-source-plugin").build(), //
                MavenPlugin.builder().artifactId("maven-gpg-plugin").configuration(this.xpp3DomMock).build(), //
                MavenPlugin.builder().artifactId("maven-javadoc-plugin").build()//
        ));
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    void testValidateFails() {
        when(this.repositoryMock.getSingleFileContentAsString(MAVEN_WORKFLOW_PATH))
                .thenThrow(GitRepositoryException.class);
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-9")),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-13")),
                () -> assertThat(report.getFailuresReport(), containsString("nexus-staging-maven-plugin")),
                () -> assertThat(report.getFailuresReport(), containsString("maven-source-plugin")),
                () -> assertThat(report.getFailuresReport(), containsString("maven-gpg-plugin")),
                () -> assertThat(report.getFailuresReport(), containsString("maven-javadoc-plugin")));
    }

    @Test
    void testValidatePGpgPluginFails() {
        when(this.mavenPomMock.getPlugins())
                .thenReturn(List.of(MavenPlugin.builder().artifactId("maven-gpg-plugin").build()));
        final Report report = this.platformValidator.validate(this.repositoryMock);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-14")));
    }
}