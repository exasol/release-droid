package com.exasol.releaserobot.usecases.validate;

import java.util.List;
import java.util.logging.Logger;

import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.usecases.*;

/**
 * Implements the Validate use case.
 */
public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final List<RepositoryValidator> repositoryValidators;
    private final RepositoryGateway repositoryGateway;

    /**
     * Create a new instance of {@link ValidateInteractor}.
     *
     * @param repositoryValidators list of repository validators
     * @param repositoryGateway    the repositoryGateway
     */
    public ValidateInteractor(final List<RepositoryValidator> repositoryValidators,
            final RepositoryGateway repositoryGateway) {
        this.repositoryValidators = repositoryValidators;
        this.repositoryGateway = repositoryGateway;
    }

    @Override
    public Report validate(final UserInput userInput) throws GitHubException {
        LOGGER.info(() -> "Validation started.");
        final Repository repository = this.repositoryGateway.getRepositoryWithBranch(userInput);
        final Report validationReport = runValidation(repository);
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    private Report runValidation(final Repository repository) {
        final Report report = ReportImpl.validationReport();
        report.merge(validateRepositories(repository));
        return report;
    }

    private Report validateRepositories(final Repository repository) {
        final Report report = ReportImpl.validationReport();
        for (final RepositoryValidator repositoryValidator : this.repositoryValidators) {
            report.merge(repositoryValidator.validate(repository));
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