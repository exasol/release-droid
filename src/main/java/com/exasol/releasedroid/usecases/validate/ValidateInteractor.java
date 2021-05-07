package com.exasol.releasedroid.usecases.validate;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.logging.ReportLogger;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Implements the Validate use case.
 */
public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final ReportLogger reportLogger = new ReportLogger();

    @Override
    // [impl->dsn~rd-runs-validate-goal~1]
    public Report validate(final Repository repository, final List<PlatformName> platforms) {
        LOGGER.info(() -> "Validation started.");
        final var report = Report.validationReport();
        for (final PlatformName platformName : platforms) {
            report.merge(this.validateForPlatform(platformName, repository.getPlatformValidators()));
        }
        logResults(report);
        return report;
    }

    private void logResults(final Report releaseReport) {
        this.reportLogger.logResults(releaseReport);
    }

    private Report validateForPlatform(final PlatformName platformName,
            final Map<PlatformName, ReleasePlatformValidator> validators) {
        if (validators.containsKey(platformName)) {
            return validators.get(platformName).validate();
        }
        throw new UnsupportedOperationException(ExaError.messageBuilder("E-RD-VAL-15") //
                .message("{{platform}} platform is not supported for this project.") //
                .parameter("platform", platformName).toString());
    }
}