package com.exasol.releasedroid.usecases.validate;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.logging.ReportLogger;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.request.UserInput;

/**
 * Implements the Validate use case.
 */
public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final RepositoryGateway repositoryGateway;
    private final ReportLogger reportLogger = new ReportLogger();

    /**
     * Create a new instance of {@link ValidateInteractor}.
     *
     * @param repositoryGateway the repositoryGateway
     */
    public ValidateInteractor(final RepositoryGateway repositoryGateway) {
        this.repositoryGateway = repositoryGateway;
    }

    @Override
    // [impl->dsn~rd-runs-validate-goal~1]
    public Report validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final Repository repository = this.repositoryGateway.getRepository(userInput);
        final var report = validatePlatforms(repository, userInput.getPlatformNames());
        logResults(report);
        return report;
    }

    private void logResults(final Report releaseReport) {
        this.reportLogger.logResults(releaseReport);
    }

    private Report validatePlatforms(final Repository repository, final List<PlatformName> platformNames) {
        final var report = Report.validationReport();
        final Map<PlatformName, ReleasePlatformValidator> validators = repository.getPlatformValidators();
        for (final PlatformName platformName : platformNames) {
            report.merge(this.validateForPlatform(platformName, validators));
        }
        return report;
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