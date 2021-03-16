package com.exasol.releasedroid.usecases.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.repository.*;
import com.exasol.releasedroid.usecases.report.Report;

@ExtendWith(MockitoExtension.class)
class JavaGitRepositoryValidatorTest {
    @Mock
    private JavaRepository javaRepository;
    @Mock
    private MavenPom mavenPom;

    @Test
    void testValidate() {
        when(this.javaRepository.getMavenPom()).thenReturn(this.mavenPom);
        when(this.mavenPom.getPlugins())
                .thenReturn(Map.of("reproducible-build-maven-plugin", MavenPlugin.builder().build()));
        final JavaGitRepositoryValidator validator = new JavaGitRepositoryValidator(this.javaRepository);
        final Report report = validator.validateReproducibleBuildPlugin();
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    void testValidateFails() {
        when(this.javaRepository.getMavenPom()).thenReturn(this.mavenPom);
        when(this.mavenPom.getPlugins()).thenReturn(Collections.emptyMap());
        final JavaGitRepositoryValidator validator = new JavaGitRepositoryValidator(this.javaRepository);
        final Report report = validator.validateReproducibleBuildPlugin();
        assertThat(report.hasFailures(), equalTo(false));
    }
}