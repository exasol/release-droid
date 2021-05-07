package com.exasol.releasedroid.adapter.communityportal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.ReleaseConfig;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;

@ExtendWith(MockitoExtension.class)
class CommunityPlatformValidatorTest {
    @Mock
    private Repository repositoryMock;

    @Test
    void validateSuccessful() {
        final String version = "1.0.0";
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("changes_0.1.0.md") //
                .body("## Summary \nHere is a short summary. \n## Feature").build();
        when(this.repositoryMock.getVersion()).thenReturn(version);
        when(this.repositoryMock.getReleaseLetter(version)).thenReturn(releaseLetter);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(getReleaseConfig()));
        final CommunityPlatformValidator validator = new CommunityPlatformValidator(this.repositoryMock);
        assertFalse(validator.validate().hasFailures());
    }

    private ReleaseConfig getReleaseConfig() {
        return ReleaseConfig.builder() //
                .communityTags(List.of("Release Droid", "Java Tools", "Open Source", "GitHub")) //
                .communityProjectName("Virtual Schema for ElasticSearch") //
                .communityProjectDescription("Here is a project description.") //
                .build();
    }

    @Test
    void validateMissingCommunityPortalTemplateFileAndChangesSummary() {
        final String version = "1.0.0";
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("changes_0.1.0.md") //
                .body("## Feature").build();
        when(this.repositoryMock.getVersion()).thenReturn(version);
        when(this.repositoryMock.getReleaseLetter(version)).thenReturn(releaseLetter);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.empty());
        final CommunityPlatformValidator validator = new CommunityPlatformValidator(this.repositoryMock);
        final Report report = validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-CP-3")), //
                () -> assertThat(report.toString(), containsString("E-RD-CP-7")) //
        );
    }

    @Test
    void validateMissingCommunityPortalTemplateInfo() {
        final String version = "1.0.0";
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("changes_0.1.0.md") //
                .body("## Summary \nHere is a short summary. \n## Feature").build();
        when(this.repositoryMock.getVersion()).thenReturn(version);
        when(this.repositoryMock.getReleaseLetter(version)).thenReturn(releaseLetter);
        when(this.repositoryMock.getReleaseConfig()).thenReturn(Optional.of(ReleaseConfig.builder().build()));
        final CommunityPlatformValidator validator = new CommunityPlatformValidator(this.repositoryMock);
        final Report report = validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-CP-5: 'Project name'")), //
                () -> assertThat(report.toString(), containsString("E-RD-CP-5: 'Project description'")), //
                () -> assertThat(report.toString(), containsString("E-RD-CP-6")) //
        );
    }
}