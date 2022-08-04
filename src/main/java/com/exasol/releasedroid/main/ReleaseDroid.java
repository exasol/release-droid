package com.exasol.releasedroid.main;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.release.ReleaseUseCase;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.*;
import com.exasol.releasedroid.usecases.request.*;
import com.exasol.releasedroid.usecases.response.ReleaseDroidResponse;
import com.exasol.releasedroid.usecases.validate.ValidateUseCase;

/**
 * This class is the main entry point for calls to a Release Droid.
 */
public class ReleaseDroid {
    private static final Logger LOGGER = Logger.getLogger(ReleaseDroid.class.getName());
    private static final String EXASOL_REPOSITORY_OWNER = "exasol";

    private final RepositoryGateway repositoryGateway;
    private final ReleaseUseCase releaseUseCase;
    private final ValidateUseCase validateUseCase;
    private final List<ReleaseDroidResponseConsumer> releaseDroidResponseConsumers;

    /**
     * Create a new instance of {@link ReleaseDroid}.
     *
     * @param repositoryGateway             repository gateway
     * @param validateUseCase               validate use case
     * @param releaseUseCase                release use case
     * @param releaseDroidResponseConsumers response consumers
     */
    public ReleaseDroid(final RepositoryGateway repositoryGateway, final ValidateUseCase validateUseCase,
            final ReleaseUseCase releaseUseCase,
            final List<ReleaseDroidResponseConsumer> releaseDroidResponseConsumers) {
        this.repositoryGateway = repositoryGateway;
        this.validateUseCase = validateUseCase;
        this.releaseUseCase = releaseUseCase;
        this.releaseDroidResponseConsumers = releaseDroidResponseConsumers;
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
        validatePlatforms(platformNames);
        LOGGER.fine(() -> "Release Droid has received '" + userInput.getGoal() + "' request for the project '"
                + userInput.getFullRepositoryName() + "'.");
        final List<Report> reports = new ArrayList<>();
        if (userInput.getGoal() == Goal.RELEASE) {
            final Set<PlatformName> skipValidationOn = getPlatformsToSkipValidationOn(userInput);
            reports.addAll(this.releaseUseCase.release(repository, platformNames, skipValidationOn));
        } else {
            reports.add(this.validateUseCase.validate(repository, platformNames, Set.of(PlatformName.JIRA)));
        }
        processResponse(createResponse(reports, userInput, platformNames));
    }

    private Set<PlatformName> getPlatformsToSkipValidationOn(final UserInput userInput) {
        if (userInput.skipValidation()) {
            return Arrays.stream(PlatformName.values()).collect(Collectors.toSet());
        } else {
            return Set.of();
        }
    }

    private void processResponse(final ReleaseDroidResponse response) {
        for (final ReleaseDroidResponseConsumer releaseDroidResponseConsumer : this.releaseDroidResponseConsumers) {
            releaseDroidResponseConsumer.consumeResponse(response);
        }
    }

    private ReleaseDroidResponse createResponse(final List<Report> reports, final UserInput userInput,
            final List<PlatformName> platformNames) {
        return ReleaseDroidResponse.builder() //
                .fullRepositoryName(userInput.getFullRepositoryName()) //
                .goal(userInput.getGoal()) //
                .platformNames(platformNames) //
                .localRepositoryPath(userInput.getLocalPath()) //
                .branch(userInput.getBranch()) //
                .reports(reports) //
                .build();
    }

    private List<PlatformName> getPlatformNames(final UserInput userInput, final Repository repository) {
        if (userInput.hasPlatforms()) {
            return userInput.getPlatformNames();
        } else {
            return repository.getReleaseConfig() //
                    .map(ReleaseConfig::getReleasePlatforms) //
                    .orElse(Collections.emptyList());
        }
    }

    private void validatePlatforms(final List<PlatformName> platformNames) {
        validatePlatformNames(platformNames);
        checkOutdatedPlatform(platformNames);
    }

    private void validatePlatformNames(final List<PlatformName> platformNames) {
        if ((platformNames == null) || platformNames.isEmpty()) {
            throwExceptionForMissingParameter("platforms");
        }
    }

    private void checkOutdatedPlatform(final List<PlatformName> platformNames) {
        if (platformNames.contains(PlatformName.COMMUNITY)) {
            LOGGER.info(
                    "Release for COMMUNITY platform is deprecated and will be removed in future. Skipping the release for the COMMUNITY platform.");
            platformNames.remove(PlatformName.COMMUNITY);
        }
    }

    private void validateUserInput(final UserInput userInput) {
        checkOwner(userInput);
        checkGoal(userInput);
        validateRepositoryName(userInput);
        validateGoalAndBranch(userInput);
        validateLocalPath(userInput);
        validateSkipValidationParameter(userInput);
    }

    private void checkOwner(final UserInput userInput) {
        if (!userInput.hasOwner()) {
            userInput.setOwner(EXASOL_REPOSITORY_OWNER);
        }
    }

    private void checkGoal(final UserInput userInput) {
        if (!userInput.hasGoal()) {
            userInput.setGoal(Goal.VALIDATE);
        }
    }

    private void validateRepositoryName(final UserInput userInput) {
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

    private void validateSkipValidationParameter(final UserInput userInput) {
        if (userInput.skipValidation() && (userInput.getGoal() != Goal.RELEASE)) {
            throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-15")
                    .message("The 'skipvalidation' argument can be only used with RELEASE goal.").toString());
        }
    }

    private void throwExceptionForMissingParameter(final String parameter) {
        throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-2")
                .message("Please specify a mandatory parameter {{parameter}} and re-run the Release Droid.", parameter)
                .toString());
    }
}