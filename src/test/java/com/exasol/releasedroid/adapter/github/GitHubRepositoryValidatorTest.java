package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.adapter.github.GitHubRepositoryValidator.PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH;
import static com.exasol.releasedroid.adapter.github.GitHubRepositoryValidator.PRINT_QUICK_CHECKSUM_WORKFLOW_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.*;

@ExtendWith(MockitoExtension.class)
class GitHubRepositoryValidatorTest {
    @Mock
    private Repository gitRepositoryMock;
    private GitHubRepositoryValidator validator;

    @BeforeEach
    void beforeEach() {
        this.validator = new GitHubRepositoryValidator(this.gitRepositoryMock);
    }

    @Test
    // [utest->dsn~validate-changelog~1]
    void testValidateChangeLog() {
        final String changelog = "[4.0.1](changes_4.0.1.md)";
        final Report validationReport = this.validator.validateChangelog(changelog, "4.0.1");
        assertThat(validationReport.hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-changelog~1]
    void testValidateChangeLogThrowsException() {
        final String changelog = "";
        final Report report = this.validator.validateChangelog(changelog, "1.0.0");
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-5: The file "
                        + "'changelog.md' doesn't contain the following link. Please add '[1.0.0](changes_1.0.0.md)' to the file")));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-version~1]
    // [utest->dsn~validate-changes-file-contains-release-letter-body~1]
    void testValidateChangesValid() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        final Report validationReport = this.validator.validateChanges(changesMock, "2.1.0", true);
        assertThat(validationReport.hasFailures(), equalTo(false));
    }

    @Test
    void testValidateChangesInvalidDateWarning() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.of(2020, 8, 1)));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        final Report validationReport = this.validator.validateChanges(changesMock, "2.1.0", true);
        assertThat(validationReport.hasFailures(), equalTo(false));
    }

    @Test
    void testValidateChangesNoDateWarning() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.empty());
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        final Report validationReport = this.validator.validateChanges(changesMock, "2.1.0", true);
        assertThat(validationReport.hasFailures(), equalTo(false));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-version~1]
    void testValidateChangesInvalidVersion() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        final Report report = this.validator.validateChanges(changesMock, "3.1.0", true);
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-6")));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-letter-body~1]
    void testValidateChangesMissingBody() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(changesMock.getFileName()).thenReturn("file");
        final Report report = this.validator.validateChanges(changesMock, "2.1.0", true);
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-8")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "{${product.version}}", "v1.4.0", "2.0.0-1", "1.2", " " })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateInvalidVersionFormat(final String version) {
        final Report report = this.validator.validateNewVersion(version);
        assertAll(() -> assertTrue(report.hasFailures()), //
                () -> assertThat(report.toString(), containsString("E-RR-VAL-3")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.0.0", "0.1.0", "0.0.1" })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateVersionWithoutPreviousTag(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.empty());
        final Report validationReport = this.validator.validateNewVersion(version);
        assertThat(validationReport.hasFailures(), equalTo(false));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.36.13", "1.37.0", "2.0.0" })
    // [utest->dsn~validate-release-version-increased-correctly~1]
    void testValidateVersionWithPreviousTag(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.of("1.36.12"));
        final Report validationReport = this.validator.validateNewVersion(version);
        assertThat(validationReport.hasFailures(), equalTo(false));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.3.7", "1.3.4", "1.4.3", "1.2.0", "3.0.0", "2.0.1", "2.1.0" })
    // [utest->dsn~validate-release-version-increased-correctly~1]
    void testValidateVersionWithPreviousTagInvalid(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.of("1.3.5"));
        final Report report = this.validator.validateNewVersion(version);
        assertAll(() -> assertTrue(report.hasFailures()),
                () -> assertThat(report.toString(),
                        containsString(
                                "E-RR-VAL-4: The new version '" + version + "' does not fit the versioning rules. "
                                        + "Possible versions for the release are: [2.0.0, 1.4.0, 1.3.6]")));
    }

    @Test
    void testValidateWorkflowFile() {
        when(this.gitRepositoryMock.getSingleFileContentAsString(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH))
                .thenReturn("I exist");
        when(this.gitRepositoryMock.getSingleFileContentAsString(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH))
                .thenReturn("I exist");
        final Report report = this.validator.validateWorkflows();
        assertFalse(report.hasFailures());
    }

    @Test
    void testValidateWorkflowFileCreateOriginalChecksumMissing() {
        when(this.gitRepositoryMock.getSingleFileContentAsString(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH))
                .thenThrow(RepositoryException.class);
        when(this.gitRepositoryMock.getSingleFileContentAsString(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH))
                .thenReturn("I exist");
        final Report report = this.validator.validateWorkflows();
        assertTrue(report.hasFailures());
    }

    @Test
    void testValidateWorkflowFilePrintQuickChecksumMissing() {
        when(this.gitRepositoryMock.getSingleFileContentAsString(PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH))
                .thenReturn("I exist");
        when(this.gitRepositoryMock.getSingleFileContentAsString(PRINT_QUICK_CHECKSUM_WORKFLOW_PATH))
                .thenThrow(RepositoryException.class);
        final Report report = this.validator.validateWorkflows();
        assertTrue(report.hasFailures());
    }
}