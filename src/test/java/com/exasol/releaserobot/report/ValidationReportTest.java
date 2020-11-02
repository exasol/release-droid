package com.exasol.releaserobot.report;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// [utest->dsn~rr-creates-validation-report~1]
class ValidationReportTest {
    private ValidationReport report;

    @BeforeEach
    void beforeEach() {
        this.report = new ValidationReport();
    }

    @Test
    void testGetFailedValidations() {
        this.report.addSuccessfulValidation("Pom file.");
        this.report.addFailedValidations("Some code", "Please, update the version.");
        assertThat(this.report.getFailuresReport(), containsString("Fail.    Some code: Please, update the version."));
    }

    @Test
    void testHasFailedValidations() {
        assertThat(this.report.hasFailures(), equalTo(false));
        this.report.addSuccessfulValidation("Pom file.");
        assertThat(this.report.hasFailures(), equalTo(false));
        this.report.addFailedValidations("Some code", "Please, update the version.");
        assertThat(this.report.hasFailures(), equalTo(true));
        this.report.addSuccessfulValidation("Changes file.");
        assertThat(this.report.hasFailures(), equalTo(true));
    }

    @Test
    void testGetFullReport() {
        this.report.addSuccessfulValidation("Pom file.");
        this.report.addFailedValidations("Some code", "Please, update the version.");
        assertAll(() -> assertThat(this.report.getFullReport(), containsString("Success. Pom file.")),
                () -> assertThat(this.report.getFullReport(),
                        containsString("Fail.    Some code: Please, update the version.")));
    }

    @Test
    void testGetShortDescriptionOnFail() {
        this.report.addFailedValidations("Some code", "Please, update the version.");
        assertThat(this.report.getShortDescription(), containsString("Validation Report: VALIDATION FAILED!"));
    }

    @Test
    void testGetShortDescriptionOnSuccess() {
        this.report.addSuccessfulValidation("Pom file.");
        assertThat(this.report.getShortDescription(), containsString("Validation Report: Validation is successful!"));
    }
}