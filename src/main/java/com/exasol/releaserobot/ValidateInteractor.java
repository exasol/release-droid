package com.exasol.releaserobot;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.exasol.releaserobot.Platform.PlatformName;
import com.exasol.releaserobot.report.Report;
import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.GitRepository;
import com.exasol.releaserobot.repository.GitRepositoryValidator;

public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final Map<PlatformName, PlatformValidator> platformValidators;
    private final GitRepository repository;

    public ValidateInteractor(final Map<PlatformName, PlatformValidator> platformValidators,
            final GitRepository repository) {
        this.platformValidators = platformValidators;
        this.repository = repository;
    }

    @Override
    public ValidationReport validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final ValidationReport validationReport = validateOnPlatforms(userInput.getPlatformNames());
        logResults(Goal.VALIDATE, validationReport);
        return validationReport;
    }

    private ValidationReport validateOnPlatforms(final Set<PlatformName> platformNames) {
        return validateOnPlatforms(this.repository.getDefaultBranchName(), platformNames);
    }

    private ValidationReport validateOnPlatforms(final String branch, final Set<PlatformName> platformNames) {
        final ValidationReport validationReport = new ValidationReport();
        final GitRepositoryValidator validator = new GitRepositoryValidator(this.repository, validationReport);
        validator.validate(branch);
        validatePlatforms(branch, validationReport, platformNames);
        return validationReport;
    }

    private void validatePlatforms(final String branch, final ValidationReport validationReport,
            final Set<PlatformName> platformNames) {
        for (final PlatformName platformName : platformNames) {
            this.getPlatformValidator(platformName).validate(validationReport);
        }
    }

    private PlatformValidator getPlatformValidator(final PlatformName platformName) {
        return this.platformValidators.get(platformName);
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
