package com.exasol.releasedroid.adapter.jira;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.request.PlatformName;

class JiraPlatformValidatorTest {
    @Test
    void validationSucceeded() {
        final JiraPlatformValidator validator = new JiraPlatformValidator(null);
        final Report report = validator.validateCommunityRelease(Map.of(PlatformName.COMMUNITY, "link"));
        assertFalse(report.hasFailures());
    }

    @Test
    void validationFailed() {
        final JiraPlatformValidator validator = new JiraPlatformValidator(null);
        final Report report = validator.validateCommunityRelease(Map.of());
        assertThat(report.toString(), containsString("E-RD-JIRA-1"));
    }

    @Test
    void validationFailedNoLink() {
        final JiraPlatformValidator validator = new JiraPlatformValidator(null);
        final Report report = validator.validateCommunityRelease(Map.of(PlatformName.COMMUNITY, ""));
        assertThat(report.toString(), containsString("E-RD-JIRA-2"));
    }
}