package com.exasol.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProjectValidatorTest {
    @Test
    void testValidateChangeLog() {
        final ProjectValidator validator = new ProjectValidator(null, "");
        final String changelog = "[4.0.1](changes-4.0.1.md)";
        assertDoesNotThrow(() -> validator.validateChangelog(changelog, "4.0.1"));
    }

    @Test
    void testValidateChangeLogThrowsException() {
        final ProjectValidator validator = new ProjectValidator(null, "");
        final String changelog = "";
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.validateChangelog(changelog, "1.0.0"));
        assertThat(exception.getMessage(), containsString("doc/changes/changelog.md file "
                + "doesn't contain the following link, please add it to the file: [1.0.0](changes-1.0.0.md)"));
    }

    @Test
    void testValidateVersionWithoutPreviousTag() {
        final ProjectValidator validator = new ProjectValidator(null, "");
        assertDoesNotThrow(() -> validator.validateVersion("1.0.0", Optional.empty()));
    }

    @Test
    void testValidateChangesValid() {
        final String changes = "# Exasol Test Containers 2.1.0, released "
                + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        final ProjectValidator validator = new ProjectValidator(null, "");
        assertDoesNotThrow(() -> validator.validateChanges(changes, "2.1.0"));
    }

    @Test
    void testValidateChangesInvalidDate() {
        final String changes = "# Exasol Test Containers 2.1.0, released 2020-06-01";
        final ProjectValidator validator = new ProjectValidator(null, "");
        assertThrows(IllegalStateException.class, () -> validator.validateChanges(changes, "2.1.0"));
    }

    @Test
    void testValidateChangesInvalidVersion() {
        final String changes = "# Exasol Test Containers 2.1.0, released "
                + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        final ProjectValidator validator = new ProjectValidator(null, "");
        assertThrows(IllegalStateException.class, () -> validator.validateChanges(changes, "3.1.0"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.3.6", "1.4.0", "2.0.0" })
    void testValidateVersionValid(final String version) {
        final ProjectValidator validator = new ProjectValidator(null, "");
        assertDoesNotThrow(() -> validator.validateVersion(version, Optional.of("1.3.5")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.3.7", "1.3.4", "1.4.3", "v1.4.0", "1.2.0", "3.0.0", "2.0.1", "2.1.0" })
    void testValidateVersionInvalid(final String version) {
        final ProjectValidator validator = new ProjectValidator(null, "");
        assertThrows(IllegalStateException.class, () -> validator.validateVersion(version, Optional.of("1.3.5")));
    }
}