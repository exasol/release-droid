package com.exasol.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import com.exasol.repository.GitRepository;
import com.exasol.repository.ReleaseLetter;

class GitRepositoryValidatorTest {
    private final GitRepository gitRepositoryMock = Mockito.mock(GitRepository.class);
    private final GitRepositoryValidator validator = new GitRepositoryValidator(this.gitRepositoryMock);

    @Test
    // [utest->dsn~validate-changelog~1]
    void testValidateChangeLog() {
        final String changelog = "[4.0.1](changes_4.0.1.md)";
        assertDoesNotThrow(() -> this.validator.validateChangelog(changelog, "4.0.1"));
    }

    @Test
    // [utest->dsn~validate-changelog~1]
    void testValidateChangeLogThrowsException() {
        final String changelog = "";
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> this.validator.validateChangelog(changelog, "1.0.0"));
        assertThat(exception.getMessage(), containsString("changelog.md file "
                + "doesn't contain the following link, please add it to the file: [1.0.0](changes_1.0.0.md)"));
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
        assertDoesNotThrow(() -> this.validator.validateChanges(changesMock, "2.1.0"));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-date~1]
    void testValidateChangesInvalidDate() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.of(2020, 8, 1)));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        assertThrows(IllegalStateException.class, () -> this.validator.validateChanges(changesMock, "2.1.0"));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-version~1]
    void testValidateChangesInvalidVersion() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.of("## Features"));
        when(changesMock.getFileName()).thenReturn("file");
        assertThrows(IllegalStateException.class, () -> this.validator.validateChanges(changesMock, "3.1.0"));
    }

    @Test
    // [utest->dsn~validate-changes-file-contains-release-letter-body~1]
    void testValidateChangesMissingBody() {
        final ReleaseLetter changesMock = Mockito.mock(ReleaseLetter.class);
        when(changesMock.getVersionNumber()).thenReturn(Optional.of("2.1.0"));
        when(changesMock.getReleaseDate()).thenReturn(Optional.of(LocalDate.now()));
        when(changesMock.getBody()).thenReturn(Optional.empty());
        when(changesMock.getFileName()).thenReturn("file");
        assertThrows(IllegalStateException.class, () -> this.validator.validateChanges(changesMock, "2.1.0"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "{${product.version}}", "v1.4.0", "2.0.0-1", "1.2", " " })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateInvalidVersionFormat(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.empty());
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.validator.validateNewVersion(version));
        assertThat(exception.getMessage(), containsString("E-RR-VAL-3"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.0.0", "0.1.0", "0.0.1" })
    // [utest->dsn~validate-release-version-format~1]
    void testValidateVersionWithoutPreviousTag(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> this.validator.validateNewVersion(version));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.36.13", "1.37.0", "2.0.0" })
    // [utest->dsn~validate-release-version-increased-correctly~1]
    void testValidateVersionWithPreviousTag(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.of("1.36.12"));
        assertDoesNotThrow(() -> this.validator.validateNewVersion(version));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.3.7", "1.3.4", "1.4.3", "1.2.0", "3.0.0", "2.0.1", "2.1.0" })
    // [utest->dsn~validate-release-version-increased-correctly~1]
    void testValidateVersionWithPreviousTagInvalid(final String version) {
        when(this.gitRepositoryMock.getLatestTag()).thenReturn(Optional.of("1.3.5"));
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> this.validator.validateNewVersion(version));
        assertThat(exception.getMessage(), containsString("E-RR-VAL-4"));
    }
}