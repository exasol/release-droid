package com.exasol.releaserobot.repository;

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

class GitRepositoryValidatorTest {
    private final GitRepository gitRepositoryMock = Mockito.mock(GitRepository.class);
    private GitRepositoryValidator validator;

    @BeforeEach
    void beforeEach() {
        this.validator = new GitRepositoryValidator(this.gitRepositoryMock);
    }

    @Test
    // [utest->dsn~validate-changelog~1]
    void testValidateChangeLog() {
        final String changelog = "[4.0.1](changes_4.0.1.md)";
        this.validator.validateChangelog(changelog, "4.0.1");
        assertThat(this.validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @Test
    // [utest->dsn~validate-changelog~1]
    void testValidateChangeLogThrowsException() {
        final String changelog = "";
        this.validator.validateChangelog(changelog, "1.0.0");
        assertAll(() -> assertThat(this.validator.validationResults.get(0).isSuccessful(), equalTo(false)),
                () -> assertThat(this.validator.validationResults.get(0).toString(), containsString("E-RR-VAL-5: The file "
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
        this.validator.validateChanges(changesMock, "2.1.0", true);
        assertThat(this.validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-date~1]
    void testValidateChangesInvalidDate() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.of(2020, 8, 1)));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        this.validator.validateChanges(changesMock, "2.1.0", true);
        assertAll(() -> assertThat(this.validator.validationResults.get(1).isSuccessful(), equalTo(false)),
                () -> assertThat(this.validator.validationResults.get(1).toString(), containsString("E-RR-VAL-7")));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-date~1]
    void testValidateChangesInvalidDateWarning() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.of(2020, 8, 1)));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        this.validator.validateChanges(changesMock, "2.1.0", false);
        assertThat(this.validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-version~1]
    void testValidateChangesInvalidVersion() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        this.validator.validateChanges(changesMock, "3.1.0", true);
        assertAll(() -> assertThat(this.validator.validationResults.get(0).isSuccessful(), equalTo(false)),
                () -> assertThat(this.validator.validationResults.get(0).toString(), containsString("E-RR-VAL-6")));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-letter-body~1]
    void testValidateChangesMissingBody() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(changesMock.getFileName()).thenReturn("file");
        this.validator.validateChanges(changesMock, "2.1.0", true);
        assertAll(() -> assertThat(this.validator.validationResults.get(2).isSuccessful(), equalTo(false)),
                () -> assertThat(this.validator.validationResults.get(2).toString(), containsString("E-RR-VAL-8")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "{${product.version}}", "v1.4.0", "2.0.0-1", "1.2", " " })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateInvalidVersionFormat(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.empty());
        final boolean validationResult = this.validator.validateNewVersion(version);
        assertAll(() -> assertThat(this.validator.validationResults.get(0).isSuccessful(), equalTo(false)),
                () -> assertThat(this.validator.validationResults.get(0).toString(), containsString("E-RR-VAL-3")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.0.0", "0.1.0", "0.0.1" })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateVersionWithoutPreviousTag(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.empty());
        this.validator.validateNewVersion(version);
        assertThat(this.validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.36.13", "1.37.0", "2.0.0" })
    // [utest->dsn~validate-release-version-increased-correctly~1]
    void testValidateVersionWithPreviousTag(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.of("1.36.12"));
        this.validator.validateNewVersion(version);
        assertThat(this.validator.validationResults.get(0).isSuccessful(), equalTo(true));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.3.7", "1.3.4", "1.4.3", "1.2.0", "3.0.0", "2.0.1", "2.1.0" })
    // [utest->dsn~validate-release-version-increased-correctly~1]
    void testValidateVersionWithPreviousTagInvalid(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.of("1.3.5"));
        final boolean validationResult = this.validator.validateNewVersion(version);
        assertAll(() -> assertThat(this.validator.validationResults.get(1).isSuccessful(), equalTo(false)),
                () -> assertThat(this.validator.validationResults.get(1).toString(),
                        containsString("E-RR-VAL-4: " + "A new version does not fit the versioning rules. "
                                + "Possible versions for the release are: [2.0.0, 1.4.0, 1.3.6]")));
    }
}