package com.exasol.releaserobot.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releaserobot.repository.Repository;
import com.exasol.releaserobot.repository.maven.JavaMavenGitBranchContent;
import com.exasol.releaserobot.repository.maven.MavenPom;
import com.exasol.releaserobot.usecases.Report;

@ExtendWith(MockitoExtension.class)
class MavenRepositoryValidatorTest {
    @Mock
    private Repository gitRepository;
    @Mock
    final JavaMavenGitBranchContent content = Mockito.mock(JavaMavenGitBranchContent.class);

    @Test
    void testValidate() {
        final MavenPom mavenPom = MavenPom.builder().artifactId("my-test-project").version("1.2.3").build();
        when(this.content.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport(mavenPom);
        assertThat(report.hasFailures(), equalTo(false));
    }

    private Report getReport(final MavenPom mavenPom) {
        when(this.gitRepository.getDefaultBranchName()).thenReturn("main");
        when(this.gitRepository.getRepositoryContent("main")).thenReturn(this.content);
        final MavenRepositoryValidator pomValidator = new MavenRepositoryValidator(this.gitRepository);
        return pomValidator.validateDefaultBranch();
    }

    @Test
    void testValidateFails() {
        final MavenPom mavenPom = MavenPom.builder().build();
        when(this.content.getMavenPom()).thenReturn(mavenPom);
        final Report report = getReport(mavenPom);
        assertAll(() -> assertThat(report.hasFailures(), equalTo(true)), //
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-11")),
                () -> assertThat(report.getFailuresReport(), containsString("E-RR-VAL-12")));
    }
}