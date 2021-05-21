package com.exasol.releasedroid.main;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.EXASOL_REPOSITORY_OWNER;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.release.ReleaseUseCase;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.ReleaseConfig;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.Goal;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.request.UserInput;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

/**
 * This class is the main entry point for calls to a Release Droid.
 */
public class ReleaseDroid {
    private static final Logger LOGGER = Logger.getLogger(ReleaseDroid.class.getName());
    private final ReleaseUseCase releaseUseCase;
    private final ReportConsumer reportConsumer;
    private final ValidateUseCase validateUseCase;
    private final RepositoryGateway repositoryGateway;

    /**
     * Create a new instance of {@link ReleaseDroid}.
     *
     * @param repositoryGateway repository gateway
     * @param releaseUseCase    release usecase
     * @param validateUseCase   validate usecase
     * @param reportConsumer    report consumer
     */
    public ReleaseDroid(final RepositoryGateway repositoryGateway, final ValidateUseCase validateUseCase,
            final ReleaseUseCase releaseUseCase, final ReportConsumer reportConsumer) {
        this.repositoryGateway = repositoryGateway;
        this.validateUseCase = validateUseCase;
        this.releaseUseCase = releaseUseCase;
        this.reportConsumer = reportConsumer;
    }

    /**
     * Main entry point for all Release Droid's calls.
     */
    // [impl->dsn~rd-creates-validation-report~1]
    // [impl->dsn~rd-creates-release-report~1]
    public void run(final UserInput userInput) {
        validateUserInput(userInput);
        final Repository repository = this.repositoryGateway.getRepository(userInput);
        final List<PlatformName> platformNames = getPlatformNames(userInput, repository);
        validatePlatformNames(platformNames);
        LOGGER.fine(() -> "Release Droid has received '" + userInput.getGoal() + "' request for the project '"
                + userInput.getFullRepositoryName() + "'.");
        final List<Report> reports = new ArrayList<>();
        if (userInput.getGoal() == Goal.VALIDATE) {
            reports.add(this.validateUseCase.validate(repository, platformNames));
        } else if (userInput.getGoal() == Goal.RELEASE) {
            reports.addAll(this.releaseUseCase.release(repository, platformNames));
        }
        this.reportConsumer.consumeReports(reports, userInput, platformNames);
    }

    private List<PlatformName> getPlatformNames(final UserInput userInput, final Repository repository) {
        final Optional<ReleaseConfig> releaseConfig = repository.getReleaseConfig();
        if (userInput.hasPlatforms()) {
            return userInput.getPlatformNames();
        } else if (releaseConfig.isPresent() && releaseConfig.get().hasReleasePlatforms()) {
            return releaseConfig.get().getReleasePlatforms();
        } else {
            return List.of();
        }
    }

    private void validatePlatformNames(final List<PlatformName> platformNames) {
        if (platformNames == null || platformNames.isEmpty()) {
            throwExceptionForMissingParameter("platforms");
        }
    }

    private void validateUserInput(final UserInput userInput) {
        if (!userInput.hasOwner()) {
            userInput.setOwner(EXASOL_REPOSITORY_OWNER);
        }
        validateMandatoryParameters(userInput);
        validateGoalAndBranch(userInput);
        validateLocalPath(userInput);
    }

    private void validateMandatoryParameters(final UserInput userInput) {
        if (!userInput.hasGoal()) {
            throwExceptionForMissingParameter("goal");
        }
        if (!userInput.hasRepositoryName()) {
            throwExceptionForMissingParameter("repository name");
        }
    }

    private void validateGoalAndBranch(final UserInput userInput) {
        if ((userInput.getGoal() == Goal.RELEASE) && (userInput.hasBranch())) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-1")
                    .message("Please, remove branch parameter if you want to make a release.").toString());
        }
    }

    private void validateLocalPath(final UserInput userInput) {
        if ((userInput.hasLocalPath()) && ((userInput.getGoal() == Goal.RELEASE) || (userInput.hasBranch()))) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-6")
                    .message("The 'local' argument can't be used together with 'branch' or RELEASE 'goal'.")
                    .toString());
        }
    }

    private void throwExceptionForMissingParameter(final String parameter) {
        throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-2")
                .message("Please specify a mandatory parameter {{parameter}} and re-run the Release Droid.", parameter)
                .toString());
    }
}