package com.exasol.releaserobot;

import java.util.logging.Logger;

import com.exasol.releaserobot.report.Report;
import com.exasol.releaserobot.report.ValidationReport;

public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final RepositoryHandler repositoryHandler;

    public ValidateInteractor(final RepositoryHandler repositoryHandler) {
        this.repositoryHandler = repositoryHandler;
    }

    @Override
    public ValidationReport validate(final UserInput userInput) {
        final ValidationReport validationReport = runValidation(userInput, this.repositoryHandler);
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    // [impl->dsn~rr-runs-validate-goal~1]
    private ValidationReport runValidation(final UserInput userInput, final RepositoryHandler repositoryHandler) {
        if (validateUserSpecifiedBranch(userInput)) {
            return repositoryHandler.validate(userInput.getGitBranch());
        }
        return repositoryHandler.validate();
    }

    private boolean validateUserSpecifiedBranch(final UserInput userInput) {
        return userInput.getGoal() != Goal.RELEASE && userInput.hasGitBranch();
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
