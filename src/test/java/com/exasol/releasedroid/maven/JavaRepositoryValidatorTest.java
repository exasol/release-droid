package com.exasol.releasedroid.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.repository.*;
import com.exasol.releasedroid.usecases.report.Report;

@ExtendWith(MockitoExtension.class)
class JavaRepositoryValidatorTest {
    @Mock
    private JavaRepository repositoryMock;

    @Test
    void testValidate() {
        final MavenPom mavenPom = MavenPom.builder().artifactId("my-test-project").version("1.2.3")
                .plugins(Map.of("reproducible-build-maven-plugin", MavenPlugin.builder().build())).build();
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
                () -> assertThat(report.toString(), containsString("E-RR-VAL-11")), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-12")), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-13")));
    }
}