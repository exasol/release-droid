package com.exasol.releasedroid.adapter.repository;

import static com.exasol.releasedroid.adapter.github.GitHubConstants.PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH;
import static com.exasol.releasedroid.adapter.github.GitHubConstants.PRINT_QUICK_CHECKSUM_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.Repository;

@ExtendWith(MockitoExtension.class)
class CommonRepositoryValidatorTest {
    private static final String VERSION = "2.1.0";
    @Mock
    private Repository repositoryMock;
    @Mock
    private ReleaseLetter releaseLetterMock;
    private CommonRepositoryValidator validator;

    @BeforeEach
    void beforeEach() {
        this.validator = new CommonRepositoryValidator(this.repositoryMock);
    }

    @Test
    // [utest->dsn~validate-changelog~1]
    // [utest->dsn~validate-changes-file-contains-release-version~1]
    // [utest->dsn~validate-changes-file-contains-release-letter-body~1]
    void testValidateSuccessful() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
        when(this.repositoryMock.getChangelogFile()).thenReturn("[2.1.0](changes_2.1.0.md)");
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(this.releaseLetterMock);
        when(this.repositoryMock.isOnDefaultBranch()).thenReturn(true);
        when(this.repositoryMock.hasFile(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH)).thenReturn(true);
        when(this.repositoryMock.hasFile(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH)).thenReturn(true);
        when(this.releaseLetterMock.getVersionNumber()).thenReturn(Optional.of("v2.1.0"));
        when(this.releaseLetterMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(this.releaseLetterMock.getBody()).thenReturn(Optional.of("## Features"));
        assertThat(this.validator.validate().hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-changelog~1]
    void testValidateChangelogEmpty() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
        when(this.repositoryMock.getChangelogFile()).thenReturn("");
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-REP-24: The file "
                        + "'changelog.md' doesn't contain the following link. Please add '[2.1.0](changes_2.1.0.md)' to the file")));
    }

    @Test
    void testValidateChangesInvalidDateWarning() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
        when(this.repositoryMock.getChangelogFile()).thenReturn("[2.1.0](changes_2.1.0.md)");
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(this.releaseLetterMock);
        when(this.repositoryMock.isOnDefaultBranch()).thenReturn(true);
        when(this.releaseLetterMock.getVersionNumber()).thenReturn(Optional.of(VERSION));
        when(this.releaseLetterMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.of(2020, 8, 1)));
        when(this.releaseLetterMock.getBody()).thenReturn(Optional.of("## Features"));
        final Report report = this.validator.validate();
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    void testValidateChangesNoDateWarning() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
        when(this.repositoryMock.getChangelogFile()).thenReturn("[2.1.0](changes_2.1.0.md)");
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(this.releaseLetterMock);
        when(this.repositoryMock.isOnDefaultBranch()).thenReturn(true);
        when(this.releaseLetterMock.getVersionNumber()).thenReturn(Optional.of(VERSION));
        when(this.releaseLetterMock.getReleaseDate()).thenReturn(Optional.empty());
        when(this.releaseLetterMock.getBody()).thenReturn(Optional.of("## Features"));
        final Report report = this.validator.validate();
        assertThat(report.hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-version~1]
    void testValidateChangesInvalidVersion() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(this.releaseLetterMock);
        when(this.releaseLetterMock.getVersionNumber()).thenReturn(Optional.of("3.1.0"));
        when(this.releaseLetterMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(this.releaseLetterMock.getBody()).thenReturn(Optional.of("## Features"));
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-REP-26")));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-letter-body~1]
    void testValidateChangesMissingBody() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(this.releaseLetterMock);
        when(this.releaseLetterMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(this.releaseLetterMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(this.releaseLetterMock.getBody()).thenReturn(Optional.empty());
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-REP-27")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "{${product.version}}", "v 1.4.0", "2.0.0-1", "1.2", " " })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateInvalidVersionFormat(final String version) {
        when(this.repositoryMock.getVersion()).thenReturn(version);
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RD-REP-22")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.0.0", "0.1.0", "0.0.1", "v0.1.0", "v2.0.0" })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateVersionWithoutPreviousTag(final String version) {
        when(this.repositoryMock.getVersion()).thenReturn(version);
        when(this.repositoryMock.getLatestTag()).thenReturn(Optional.empty());
        final Report report = this.validator.validate();
        assertThat(report.toString(), containsString("Version format is correct"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.36.13", "1.37.0", "2.0.0" })
    // [utest->dsn~validate-release-version-increased-correctly~1]
    void testValidateVersionWithPreviousTag(final String version) {
        when(this.repositoryMock.getVersion()).thenReturn(version);
        when(this.repositoryMock.getLatestTag()).thenReturn(Optional.of("1.36.12"));
        final Report report = this.validator.validate();
        assertThat(report.toString(), containsString("Version format is correct"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.3.7", "1.3.4", "1.4.3", "1.2.0", "3.0.0", "2.0.1", "2.1.0" })
    // [utest->dsn~validate-release-version-increased-correctly~1]
    void testValidateVersionWithPreviousTagInvalid(final String version) {
        when(this.repositoryMock.getVersion()).thenReturn(version);
        when(this.repositoryMock.getLatestTag()).thenReturn(Optional.of("1.3.5"));
        final Report report = this.validator.validate();
        assertAll(() -> assertTrue(report.hasFailures()),
                () -> assertThat(report.toString(),
                        containsString(
                                "E-RD-REP-23: The new version '" + version + "' does not fit the versioning rules. "
                                        + "Possible versions for the release are: [2.0.0, 1.4.0, 1.3.6]")));
    }

    @Test
    void testValidateWorkflowFileCreateOriginalChecksumMissing() {
        when(this.repositoryMock.hasFile(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH)).thenReturn(true);
        when(this.repositoryMock.hasFile(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH)).thenReturn(false);
        final Report report = this.validator.validate();
        assertThat(report.toString(), containsString("E-RD-REP-28"));
    }

    @Test
    void testValidateWorkflowFilePrintQuickChecksumMissing() {
        when(this.repositoryMock.hasFile(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH)).thenReturn(false);
        when(this.repositoryMock.hasFile(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH)).thenReturn(true);
        final Report report = this.validator.validate();
        assertThat(report.toString(),
                containsString("E-RD-REP-28"));
    }

    @Test
    void testValidateSuccessfulWithoutWorkflows() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
        when(this.repositoryMock.getChangelogFile()).thenReturn("[2.1.0](changes_2.1.0.md)");
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(this.releaseLetterMock);
        when(this.repositoryMock.isOnDefaultBranch()).thenReturn(true);
        when(this.repositoryMock.hasFile(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH)).thenReturn(false);
        when(this.repositoryMock.hasFile(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH)).thenReturn(false);
        when(this.releaseLetterMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(this.releaseLetterMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(this.releaseLetterMock.getBody()).thenReturn(Optional.of("## Features"));
        assertThat(this.validator.validate().hasFailures(), equalTo(false));
    }
}