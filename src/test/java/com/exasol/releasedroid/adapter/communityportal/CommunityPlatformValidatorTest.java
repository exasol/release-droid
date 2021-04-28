package com.exasol.releasedroid.adapter.communityportal;

import static com.exasol.releasedroid.adapter.communityportal.CommunityPortalConstants.RELEASE_CONFIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;

@ExtendWith(MockitoExtension.class)
class CommunityPlatformValidatorTest {
    @Mock
    private Repository repositoryMock;

    @Test
    void validateSuccessful() {
        final String version = "1.0.0";
        final String communityPortalTemplate = "community-tags:\n" //
                + "- Release Droid\n" //
                + "- Java Tools\n" //
                + "- Open Source\n" //
                + "- GitHub\n" //
                + "community-project-name: Virtual Schema for ElasticSearch\n" //
                + "community-project-description: Here is a project description.\n";
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("changes_0.1.0.md") //
                .body("## Summary \nHere is a short summary. \n## Feature").build();
        when(this.repositoryMock.getVersion()).thenReturn(version);
        when(this.repositoryMock.getReleaseLetter(version)).thenReturn(releaseLetter);
        when(this.repositoryMock.getSingleFileContentAsString(RELEASE_CONFIG)).thenReturn(communityPortalTemplate);
        final CommunityPlatformValidator validator = new CommunityPlatformValidator(this.repositoryMock);
        assertFalse(validator.validate().hasFailures());
    }

    @Test
    void validateMissingCommunityPortalTemplateFileAndChangesSummary() {
        final String version = "1.0.0";
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("changes_0.1.0.md") //
                .body("## Feature").build();
        when(this.repositoryMock.getVersion()).thenReturn(version);
        when(this.repositoryMock.getReleaseLetter(version)).thenReturn(releaseLetter);
        when(this.repositoryMock.getSingleFileContentAsString(RELEASE_CONFIG)).thenThrow(RepositoryException.class);
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
        when(this.repositoryMock.getSingleFileContentAsString(RELEASE_CONFIG)).thenReturn("{}");
        final CommunityPlatformValidator validator = new CommunityPlatformValidator(this.repositoryMock);
        final Report report = validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-CP-5: 'Project name'")), //
                () -> assertThat(report.toString(), containsString("E-RD-CP-5: 'Project description'")), //
                () -> assertThat(report.toString(), containsString("E-RD-CP-6")) //
        );
    }
}