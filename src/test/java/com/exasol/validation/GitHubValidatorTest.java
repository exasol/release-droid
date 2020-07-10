package com.exasol.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GitHubValidatorTest {
    @Test
    void validateVersionFromPom() {
        final GitHubValidator validator = new GitHubValidator();
        final String pom = "<version>1.0.1</version>";
        assertThat(validator.getVersionFromPom(pom), equalTo("1.0.1"));
    }

    @Test
    void validateVersionFromPomMissing() {
        final GitHubValidator validator = new GitHubValidator();
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> validator.getVersionFromPom(""));
        assertThat(exception.getMessage(), containsString("Cannot find a project version in pom.xml file."));
    }
}