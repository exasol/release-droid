package com.exasol.releasedroid.verify;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exasol.releasedroid.usecases.report.Report;

public class ReportVerifier {

    public static void assertContainsResultMessage(final Report report, final String resultMessage) {
        assertTrue(report.getResults().stream().anyMatch(r -> r.toString().contains(resultMessage)));
    }
}
