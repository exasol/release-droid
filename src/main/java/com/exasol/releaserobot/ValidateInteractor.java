package com.exasol.releaserobot;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.exasol.releaserobot.report.Report;
import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.GitRepository;
import com.exasol.releaserobot.repository.GitRepositoryValidator;

/**
 * Implements the Validate use case.
 */
public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final Map<PlatformName, PlatformValidator> platformValidators;
    private final GitRepository repository;

    /**
     * Create a new instance of {@link ValidateInteractor}.
     * 
     * @param platformValidators map of platform names and platform validators
     * @param repository         instance of {@link GitRepository}
     */
    public ValidateInteractor(final Map<PlatformName, PlatformValidator> platformValidators,
            final GitRepository repository) {
        this.platformValidators = platformValidators;
        this.repository = repository;
    }

    @Override
    public ValidationReport validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final ValidationReport validationReport = validate(userInput.getPlatformNames(), userInput);
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    private ValidationReport validate(final Set<PlatformName> platformNames, final UserInput userInput) {
        final String branch = userInput.hasGitBranch() ? userInput.getGitBranch()
                : this.repository.getDefaultBranchName();
        final ValidationReport validationReport = new ValidationReport();
        final GitRepositoryValidator repositoryValidator = new GitRepositoryValidator(this.repository,
                validationReport);
        repositoryValidator.validate(branch);
        validatePlatforms(validationReport, platformNames);
        return validationReport;
    }

    private void validatePlatforms(final ValidationReport validationReport, final Set<PlatformName> platformNames) {
        for (final PlatformName platformName : platformNames) {
            this.platformValidators.get(platformName).validate(validationReport);
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