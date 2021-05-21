package com.exasol.releasedroid.usecases.report;

import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Report for the validation use case.
 */
public class ValidationReport extends AbstractReport {
    private ValidationReport(final String validation, final PlatformName platformName) {
        super(validation, platformName);
    }

    /**
     * Create a new instance of {@link ValidationReport}.
     *
     * @param platformName platform name
     * @return new instance of {@link ValidationReport}
     */
    public static ValidationReport create(final PlatformName platformName) {
        return new ValidationReport("Validation", platformName);
    }

    /**
     * Create a new instance of {@link ValidationReport}.
     *
     * @return new instance of {@link ValidationReport}
     */
    public static ValidationReport create() {
        return create(null);
    }
}