package com.exasol.releaserobot.usecases.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import com.exasol.releaserobot.repository.Repository;
import com.exasol.releaserobot.repository.ReleaseLetter;
import com.exasol.releaserobot.usecases.Report;

class GitRepositoryValidatorTest {
    private final Repository gitRepositoryMock = Mockito.mock(Repository.class);
    private GitRepositoryValidator validator;

    @BeforeEach
    void beforeEach() {
        this.validator = new GitRepositoryValidator(this.gitRepositoryMock);
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
        final Report validationReport = this.validator.validateChangelog(changelog, "1.0.0");
        assertAll(() -> assertThat(validationReport.hasFailures(), equalTo(true)),
                () -> assertThat(validationReport.getFailuresReport(), containsString("E-RR-VAL-5: The file "
                        + "'changelog.md' doesn't contain the following link, please add '[1.0.0](changes_1.0.0.md)' to the file")));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-version~1]
    // [utest->dsn~validate-changes-file-contains-release-date~1]
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
    // [utest->dsn~validate-changes-file-contains-release-date~1]
    void testValidateChangesInvalidDate() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.of(2020, 8, 1)));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        final Report validationReport = this.validator.validateChanges(changesMock, "2.1.0", true);
        assertAll(() -> assertThat(validationReport.hasFailures(), equalTo(true)),
                () -> assertThat(validationReport.getFailuresReport(), containsString("E-RR-VAL-7")));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-date~1]
    void testValidateChangesInvalidDateWarning() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.of(2020, 8, 1)));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        final Report validationReport = this.validator.validateChanges(changesMock, "2.1.0", false);
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
        final Report validationReport = this.validator.validateChanges(changesMock, "3.1.0", true);
        assertAll(() -> assertThat(validationReport.hasFailures(), equalTo(true)),
                () -> assertThat(validationReport.getFailuresReport(), containsString("E-RR-VAL-6")));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-letter-body~1]
    void testValidateChangesMissingBody() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(changesMock.getFileName()).thenReturn("file");
        final Report validationReport = this.validator.validateChanges(changesMock, "2.1.0", true);
        assertAll(() -> assertThat(validationReport.hasFailures(), equalTo(true)),
                () -> assertThat(validationReport.getFailuresReport(), containsString("E-RR-VAL-8")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "{${product.version}}", "v1.4.0", "2.0.0-1", "1.2", " " })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateInvalidVersionFormat(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.empty());

        final Report validationReport = this.validator.validateNewVersion(version);
        assertAll(() -> assertThat(validationReport.hasFailures(), equalTo(true)),
                () -> assertThat(validationReport.getFailuresReport(), containsString("E-RR-VAL-3")));
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
        final Report validationReport = this.validator.validateNewVersion(version);
        assertAll(() -> assertThat(validationReport.hasFailures(), equalTo(true)),
                () -> assertThat(validationReport.getFailuresReport(),
                        containsString("E-RR-VAL-4: " + "A new version does not fit the versioning rules. "
                                + "Possible versions for the release are: [2.0.0, 1.4.0, 1.3.6]")));
    }
}