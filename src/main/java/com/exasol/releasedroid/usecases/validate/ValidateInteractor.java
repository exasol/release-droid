package com.exasol.releasedroid.usecases.validate;

import static com.exasol.releasedroid.main.ReportLogger.logResults;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.exasol.releasedroid.usecases.*;
import com.exasol.releasedroid.usecases.report.Report;

/**
 * Implements the Validate use case.
 */
public class ValidateInteractor implements ValidateUseCase {
    private static final Logger LOGGER = Logger.getLogger(ValidateInteractor.class.getName());
    private final List<RepositoryValidator> repositoryValidators;
    private final Map<PlatformName, ? extends RepositoryValidator> platformValidators;
    private final RepositoryGateway repositoryGateway;

    /**
     * Create a new instance of {@link ValidateInteractor}.
     *
     * @param repositoryValidators list of repository validators
     * @param repositoryGateway    the repositoryGateway
     */
    public ValidateInteractor(final List<RepositoryValidator> repositoryValidators,
            final Map<PlatformName, ? extends RepositoryValidator> platformValidators,
            final RepositoryGateway repositoryGateway) {
        this.repositoryValidators = repositoryValidators;
        this.platformValidators = platformValidators;
        this.repositoryGateway = repositoryGateway;
    }

    @Override
    // [impl->dsn~rr-runs-validate-goal~1]
    public Report validate(final UserInput userInput) {
        LOGGER.info(() -> "Validation started.");
        final Repository repository = this.repositoryGateway.getRepositoryWithBranch(userInput);
        final Report validationReport = runValidation(repository, userInput.getPlatformNames());
        logResults(validationReport);
        return validationReport;
    }

    private Report runValidation(final Repository repository, final List<PlatformName> platformNames) {
        final Report report = Report.validationReport();
        report.merge(validateRepositories(repository));
        report.merge(validatePlatforms(repository, platformNames));
        return report;
    }

    private Report validatePlatforms(final Repository repository, final List<PlatformName> platformNames) {
        final Report report = Report.validationReport();
        for (final PlatformName platformName : platformNames) {
            report.merge(this.validateForPlatform(platformName, repository));
        }
        return report;
    }

    private Report validateForPlatform(final PlatformName platformName, final Repository repository) {
        final RepositoryValidator repositoryValidator = this.getRepositoryValidatorForPlatform(platformName);
        return repositoryValidator.validate(repository);
    }

    private RepositoryValidator getRepositoryValidatorForPlatform(final PlatformName platformName) {
        return this.platformValidators.get(platformName);
    }

    private Report validateRepositories(final Repository repository) {
        final Report report = Report.validationReport();
        for (final RepositoryValidator repositoryValidator : this.repositoryValidators) {
            report.merge(repositoryValidator.validate(repository));
        }
        return report;
    }
}