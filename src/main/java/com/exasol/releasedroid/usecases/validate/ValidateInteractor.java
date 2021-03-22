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
    // [impl->dsn~rr-runs-validate-goal~1]
    public Report validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final Repository repository = this.repositoryGateway.getRepository(userInput);
        final Report validationReport = runValidation(repository, userInput.getPlatformNames());
        logResults(validationReport);
        return validationReport;
    }

    private void logResults(final Report releaseReport) {
        this.reportLogger.logResults(releaseReport);
    }

    private Report runValidation(final Repository repository, final List<PlatformName> platformNames) {
        final Report report = Report.validationReport();
        report.merge(validateRepositories(repository));
        report.merge(validatePlatforms(repository, platformNames));
        return report;
    }

    private Report validatePlatforms(final Repository repository, final List<PlatformName> platformNames) {
        final Report report = Report.validationReport();
        final Map<PlatformName, RepositoryValidator> validators = repository.getValidatorForPlatforms();
        for (final PlatformName platformName : platformNames) {
            report.merge(this.validateForPlatform(platformName, validators));
        }
        return report;
    }

    private Report validateForPlatform(final PlatformName platformName,
            final Map<PlatformName, RepositoryValidator> validators) {
        if (validators.containsKey(platformName)) {
            return validators.get(platformName).validate();
        }
        throw new UnsupportedOperationException(ExaError.messageBuilder("E-RR-5") //
                .message("{{platform}} platform is not supported for this project.") //
                .parameter("platform", platformName).toString());
    }

    private Report validateRepositories(final Repository repository) {
        final Report report = Report.validationReport();
        final List<RepositoryValidator> repositoryValidators = repository.getStructureValidators(); // structureRepoValidator,
        for (final RepositoryValidator repositoryValidator : repositoryValidators) {
            report.merge(repositoryValidator.validate());
        }
        return report;
    }
}