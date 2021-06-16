package com.exasol.releasedroid.usecases.validate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * Implements the Validate use case.
 */
public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());

    @Override
    // [impl->dsn~rd-runs-validate-goal~1]
    public Report validate(final Repository repository, final List<PlatformName> platforms,
                           final Set<PlatformName> skipValidationOn) {
        LOGGER.info(() -> "Validation started.");
        final var report = ValidationReport.create();
        for (final PlatformName platformName : platforms) {
            if (!skipValidationOn.contains(platformName)) {
                report.merge(this.validateForPlatform(platformName, repository.getPlatformValidators()));
            }
        }
        return report;
    }

    private Report validateForPlatform(final PlatformName platformName,
            final Map<PlatformName, ReleasePlatformValidator> validators) {
        final var report = ValidationReport.create(platformName);
        if (validators.containsKey(platformName)) {
            report.merge(validators.get(platformName).validate());
        } else {
            throw new UnsupportedOperationException(ExaError.messageBuilder("E-RD-VAL-15") //
                    .message("{{platform}} platform is not supported for this project.") //
                    .parameter("platform", platformName).toString());
        }
        return report;
    }
}