package com.exasol.releaserobot.report;

import static com.exasol.releaserobot.PlatformName.GITHUB;
import static com.exasol.releaserobot.PlatformName.MAVEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.exasol.releaserobot.UserInput;

class ReportWriterTest {
    @TempDir
    Path tempDir;
    private UserInput userInput;
    private Path reportPath;
    private Report validationReport;
    private Report releaseReport;

    @BeforeEach
    void setUp() {
        this.reportPath = Path.of(this.tempDir.toString(), "test-report.txt");
        this.userInput = UserInput.builder().repositoryName("my-repository").repositoryOwner("me").goal("validate")
                .platforms("github").build();
        this.validationReport = new ReportImpl(ReportImpl.ReportName.VALIDATION);
        this.validationReport.addResult(ValidationResult.failedValidation("SOME-CODE-1", "Validations 1"));
        this.validationReport.addResult(ValidationResult.successfulValidation("Validations 2"));
        this.validationReport.addResult(ValidationResult.failedValidation("SOME-CODE-2", "Validations 3"));
        this.validationReport.addResult(ValidationResult.successfulValidation("Validations 4"));
        this.releaseReport = new ReportImpl(ReportImpl.ReportName.RELEASE);
        this.releaseReport.addResult(ReleaseResult.successfulRelease(GITHUB));
        this.releaseReport.addResult(ReleaseResult.failedRelease(MAVEN, "Wrong credentials"));
    }

    @Test
    // [utest->dsn~rr-writes-report-to-file~1]
    void testWriteValidationReportToFile() throws IOException {
        final ReportWriter reportWriter = new ReportWriter(this.userInput, this.reportPath);
        reportWriter.writeValidationReportToFile(List.of(this.validationReport, this.releaseReport));
        final List<String> report = Files.readAllLines(this.reportPath);
        assertAll(() -> assertThat(report.size(), equalTo(18)), //
                () -> assertThat(report.get(0), containsString(LocalDate.now().toString())), //
                () -> assertThat(report.get(1), equalTo("")), //
                () -> assertThat(report.get(2), equalTo("Goal: VALIDATE")), //
                () -> assertThat(report.get(3), equalTo("Repository: me.my-repository")), //
                () -> assertThat(report.get(4), equalTo("Platforms: GITHUB")), //
                () -> assertThat(report.get(5), equalTo("")), //
                () -> assertThat(report.get(6), equalTo("VALIDATION Report: VALIDATION FAILED!")), //
                () -> assertThat(report.get(7), equalTo("Fail.    SOME-CODE-1: Validations 1")), //
                () -> assertThat(report.get(8), equalTo("Success. Validations 2")), //
                () -> assertThat(report.get(9), equalTo("Fail.    SOME-CODE-2: Validations 3")), //
                () -> assertThat(report.get(10), equalTo("Success. Validations 4")), //
                () -> assertThat(report.get(11), equalTo("")), //
                () -> assertThat(report.get(12), equalTo("")), //
                () -> assertThat(report.get(13), equalTo("RELEASE Report: RELEASE FAILED!")), //
                () -> assertThat(report.get(14), equalTo("Success. GITHUB")), //
                () -> assertThat(report.get(15), equalTo("Fail.    MAVEN: Wrong credentials")), //
                () -> assertThat(report.get(16), equalTo("")) //
        );
    }
}