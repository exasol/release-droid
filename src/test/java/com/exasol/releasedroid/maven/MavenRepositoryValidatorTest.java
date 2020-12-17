package com.exasol.releasedroid.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.repository.maven.MavenPom;
import com.exasol.releasedroid.repository.maven.MavenRepository;
import com.exasol.releasedroid.usecases.report.Report;

@ExtendWith(MockitoExtension.class)
class MavenRepositoryValidatorTest {
    @Mock
    private MavenRepository repositoryMock;

    @Test
    void testValidate() {
        final MavenPom mavenPom = MavenPom.builder().artifactId("my-test-project").version("1.2.3").build();
        when(this.repositoryMock.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport(mavenPom);
        assertFalse(report.hasFailures());
    }

    private Report getReport(final MavenPom mavenPom) {
        final MavenRepositoryValidator pomValidator = new MavenRepositoryValidator();
        return pomValidator.validate(this.repositoryMock);
    }

    @Test
    void testValidateFails() {
        final MavenPom mavenPom = MavenPom.builder().build();
        when(this.repositoryMock.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport(mavenPom);
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-11")), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-12")));
    }
}