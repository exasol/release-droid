package com.exasol.releaserobot.usecases.validate;

import java.util.List;
import java.util.logging.Logger;

import com.exasol.releaserobot.usecases.*;

/**
 * Implements the Validate use case.
 */
public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final List<PlatformValidator> platformValidators;
    private final List<RepositoryValidator> repositoryValidators;

    /**
     * Create a new instance of {@link ValidateInteractor}.
     * 
     * @param platformValidators   list of platform validators
     * @param repositoryValidators list of repository validators
     */
    public ValidateInteractor(final List<PlatformValidator> platformValidators,
            final List<RepositoryValidator> repositoryValidators) {
        this.platformValidators = platformValidators;
        this.repositoryValidators = repositoryValidators;
    }

    @Override
    public Report validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final Report validationReport = runValidation(userInput);
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    private Report runValidation(final UserInput userInput) {
        final Report report = ReportImpl.validationReport();
        report.merge(validateRepositories(userInput));
        report.merge(validatePlatforms());
        return report;
    }

    private Report validateRepositories(final UserInput userInput) {
        final Report report = ReportImpl.validationReport();
        for (final RepositoryValidator repositoryValidator : this.repositoryValidators) {
            if (userInput.hasGitBranch()) {
                report.merge(repositoryValidator.validateBranch(userInput.getGitBranch()));
            } else {
                report.merge(repositoryValidator.validateDefaultBranch());
            }
        }
        return report;
    }

    private Report validatePlatforms() {
        final Report report = ReportImpl.validationReport();
        for (final PlatformValidator platformValidator : this.platformValidators) {
            report.merge(platformValidator.validate());
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