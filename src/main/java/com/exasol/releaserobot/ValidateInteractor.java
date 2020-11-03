package com.exasol.releaserobot;

import static com.exasol.releaserobot.report.ReportImpl.ReportName.VALIDATION;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.exasol.releaserobot.report.Report;
import com.exasol.releaserobot.report.ReportImpl;
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
    public Report validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final Report validationReport = validate(userInput.getPlatformNames(), userInput);
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    private Report validate(final Set<PlatformName> platformNames, final UserInput userInput) {
        final String branch = userInput.hasGitBranch() ? userInput.getGitBranch()
                : this.repository.getDefaultBranchName();
        final GitRepositoryValidator repositoryValidator = new GitRepositoryValidator(this.repository);
        final Report repositoryValidationReport = repositoryValidator.validate(branch);
        final Report platformsValidationReport = validatePlatforms(platformNames);
        repositoryValidationReport.merge(platformsValidationReport);
        return repositoryValidationReport;
    }

    private Report validatePlatforms(final Set<PlatformName> platformNames) {
        final Report report = new ReportImpl(VALIDATION);
        for (final PlatformName platformName : platformNames) {
            report.merge(this.platformValidators.get(platformName).validate());
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