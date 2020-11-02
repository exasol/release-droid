package com.exasol.releaserobot;

import java.util.Set;
import java.util.logging.Logger;

import com.exasol.releaserobot.report.Report;
import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.*;

public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final Set<Platform> platforms;
    private final GitRepository repository;

    public ValidateInteractor(final Set<Platform> platforms, final GitRepository repository) {
        this.platforms = platforms;
        this.repository = repository;
    }

    @Override
    public ValidationReport validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final ValidationReport validationReport = runValidation(userInput);
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    // [impl->dsn~rr-runs-validate-goal~1]
    private ValidationReport runValidation(final UserInput userInput) {
        if (validateUserSpecifiedBranch(userInput)) {
            return validate(userInput.getGitBranch());
        }
        return validate();
    }

    private boolean validateUserSpecifiedBranch(final UserInput userInput) {
        return userInput.getGoal() != Goal.RELEASE && userInput.hasGitBranch();
    }

    private ValidationReport validate() {
        return validate(this.repository.getDefaultBranchName());
    }

    private ValidationReport validate(final String branch) {
        final ValidationReport validationReport = new ValidationReport();
        final GitRepositoryValidator validator = new GitRepositoryValidator(this.repository, validationReport);
        validator.validate(branch);
        validatePlatforms(branch, validationReport);
        return validationReport;
    }

    private void validatePlatforms(final String branch, final ValidationReport validationReport) {
        for (final Platform platform : this.platforms) {
            platform.validate(validationReport);
        }
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
