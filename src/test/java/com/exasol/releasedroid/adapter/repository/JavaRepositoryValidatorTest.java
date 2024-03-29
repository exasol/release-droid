package com.exasol.releasedroid.adapter.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.adapter.maven.MavenPlugin;
import com.exasol.releasedroid.adapter.maven.MavenPom;
import com.exasol.releasedroid.usecases.report.Report;

@ExtendWith(MockitoExtension.class)
class JavaRepositoryValidatorTest {
    @Mock
    private JavaRepository repositoryMock;

    @Test
    void testValidate() {
        final MavenPom mavenPom = MavenPom.builder().groupId("my.group.id").artifactId("my-test-project")
                .plugins(Map.of("project-keeper-maven-plugin", MavenPlugin.builder().version("0.6.0").build())).build();
        when(this.repositoryMock.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport();
        assertFalse(report.hasFailures());
    }

    private Report getReport() {
        final JavaRepositoryValidator pomValidator = new JavaRepositoryValidator(this.repositoryMock);
        return pomValidator.validate();
    }

    @Test
    void testValidateFails() {
        final MavenPom mavenPom = MavenPom.builder().build();
        when(this.repositoryMock.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-REP-13")), //
                () -> assertThat(report.toString(), containsString("E-RD-REP-31")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "0.6.0", "0.6.13", "1.0.0", "1.4.0" })
    void testValidateProjectKeeperVersion(final String keeperVersion) {
        final MavenPom mavenPom = MavenPom.builder().groupId("my.group.id").artifactId("my-test-project")
                .plugins(Map.of("reproducible-build-maven-plugin", MavenPlugin.builder().build(), //
                        "project-keeper-maven-plugin", MavenPlugin.builder().version(keeperVersion).build()))
                .build();
        when(this.repositoryMock.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport();
        assertFalse(report.hasFailures());
    }

    @Test
    void testValidatMissingGroupId() {
        final MavenPom mavenPom = MavenPom.builder().artifactId("my-test-project")
                .plugins(Map.of("project-keeper-maven-plugin", MavenPlugin.builder().build())).build();
        when(this.repositoryMock.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(),
                        CoreMatchers.containsString("E-RD-REP-31: Cannot detect a 'groupId' in the pom file.")));
    }

    @Test
    void testValidateProjectKeeperRepository() {
        final MavenPom mavenPom = MavenPom.builder().groupId("my.group.id").artifactId("project-keeper-maven-plugin")
                .plugins(Map.of("project-keeper-maven-plugin", MavenPlugin.builder().version("${version}").build()))
                .build();
        when(this.repositoryMock.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport();
        assertFalse(report.hasFailures());
    }
}