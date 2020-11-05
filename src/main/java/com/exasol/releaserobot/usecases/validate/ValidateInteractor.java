package com.exasol.releaserobot.usecases.validate;

import java.util.List;
import java.util.logging.Logger;

import com.exasol.releaserobot.repository.Repository;
import com.exasol.releaserobot.usecases.*;

/**
 * Implements the Validate use case.
 */
public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final List<PlatformValidator> platformValidators;
    private final List<RepositoryValidator> repositoryValidators;
    private final RepositoryGateway repositoryGateway;

    /**
     * Create a new instance of {@link ValidateInteractor}.
     *
     * @param platformValidators   list of platform validators
     * @param repositoryValidators list of repository validators
     * @param repositoryGateway    the repositoryGateway
     */
    public ValidateInteractor(final List<PlatformValidator> platformValidators,
            final List<RepositoryValidator> repositoryValidators, final RepositoryGateway repositoryGateway) {
        this.platformValidators = platformValidators;
        this.repositoryValidators = repositoryValidators;
        this.repositoryGateway = repositoryGateway;
    }

    @Override
    public Report validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final Repository repository = this.repositoryGateway.getRepository(userInput);
        final Report validationReport = runValidation(repository, userInput);
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    private Report runValidation(final Repository repository, final UserInput userInput) {
        final Report report = ReportImpl.validationReport();
        report.merge(validateRepositories(repository, userInput));
        report.merge(validatePlatforms(repository));
        return report;
    }

    private Report validateRepositories(final Repository repository, final UserInput userInput) {
        final Report report = ReportImpl.validationReport();

        for (final RepositoryValidator repositoryValidator : this.repositoryValidators) {
            report.merge(repositoryValidator.validate(repository));
        }
        return report;
    }

    private Report validatePlatforms(final Repository repository) {
        final Report report = ReportImpl.validationReport();
        for (final PlatformValidator platformValidator : this.platformValidators) {
            report.merge(platformValidator.validate(repository));
        }
        return report;
    }

    // [impl->dsn~rr-creates-validation-report~1]
    // [impl->dsn~rr-creates-release-report~1]
    private void logResults(final Goal goal, final Report report) {
        if (report.hasFailures()) {
            LOGGER.severe(() -> "'" + goal + "' request failed: " + report.getFailuresReport());
        } else {
            LOGGER.info(report.getShortDescription());
        }
    }
}