package com.exasol.releasedroid.output;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.formatting.HeaderFormatter;
import com.exasol.releasedroid.formatting.ReportFormatter;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.response.ReleaseDroidResponse;

@ExtendWith(MockitoExtension.class)
class ResponseDiskWriterTest {
    @TempDir
    Path tempDir;
    @Mock
    private ReportFormatter reportFormatter;
    @Mock
    private HeaderFormatter headerFormatter;

    @Test
    // [utest->dsn~rd-writes-report-to-file~1]
    void testWriteValidationReportToFile() throws IOException {
        when(this.reportFormatter.formatReport(any())).thenReturn("Mock report" + LINE_SEPARATOR);
        when(this.headerFormatter.formatHeader(any())).thenReturn("Mock header" + LINE_SEPARATOR);
        final ReleaseDroidResponse response = ReleaseDroidResponse.builder().reports(List.of(ValidationReport.create()))
                .build();
        final ResponseDiskWriter writer = new ResponseDiskWriter(this.reportFormatter, this.headerFormatter,
                this.tempDir.toString(), "test-report.txt");
        writer.consumeResponse(response);
        final List<String> report = Files.readAllLines(Path.of(this.tempDir.toString(), "test-report.txt"));
        assertAll(() -> assertThat(report.size(), equalTo(2)), //
                () -> assertThat(report.get(0), equalTo("Mock header")), //
                () -> assertThat(report.get(1), equalTo("Mock report")) //
        );
    }

    @Test
    // [utest->dsn~rd-writes-report-to-file~1]
    void testWriteValidationReportToFileMissingDirectory() throws IOException {
        when(this.reportFormatter.formatReport(any())).thenReturn("Mock report" + LINE_SEPARATOR);
        when(this.headerFormatter.formatHeader(any())).thenReturn("Mock header" + LINE_SEPARATOR);
        final ReleaseDroidResponse response = ReleaseDroidResponse.builder().reports(List.of(ValidationReport.create()))
                .build();
        final String reportPath = this.tempDir.toString() + "/.release-droid";
        final ResponseDiskWriter writer = new ResponseDiskWriter(this.reportFormatter, this.headerFormatter, reportPath,
                "test-report.txt");
        writer.consumeResponse(response);
        final List<String> report = Files.readAllLines(Path.of(reportPath.toString(), "test-report.txt"));
        assertAll(() -> assertThat(report.size(), equalTo(2)), //
                () -> assertThat(report.get(0), equalTo("Mock header")), //
                () -> assertThat(report.get(1), equalTo("Mock report")) //
        );
    }
}