package com.exasol.releasedroid.main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.UseCase;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.repository.RepositoryGateway;
import com.exasol.releasedroid.usecases.request.*;
import com.exasol.releasedroid.usecases.response.ReleaseDroidResponse;

/**
 * This class is the main entry point for calls to a Release Droid.
 */
public class ReleaseDroid {
    private static final Logger LOGGER = Logger.getLogger(ReleaseDroid.class.getName());
    private static final String EXASOL_REPOSITORY_OWNER = "exasol";

    private final RepositoryGateway repositoryGateway;
    private final UseCase releaseUseCase;
    private final UseCase validateUseCase;
    private final List<ReleaseDroidResponseConsumer> releaseDroidResponseConsumers;

    /**
     * Create a new instance of {@link ReleaseDroid}.
     *
     * @param repositoryGateway             repository gateway
     * @param validateUseCase               validate use case
     * @param releaseUseCase                release use case
     * @param releaseDroidResponseConsumers response consumers
     */
    public ReleaseDroid(final RepositoryGateway repositoryGateway, final UseCase validateUseCase,
            final UseCase releaseUseCase, final List<ReleaseDroidResponseConsumer> releaseDroidResponseConsumers) {
        this.repositoryGateway = repositoryGateway;
        this.validateUseCase = validateUseCase;
        this.releaseUseCase = releaseUseCase;
        this.releaseDroidResponseConsumers = releaseDroidResponseConsumers;
    }

    // [impl->dsn~rd-creates-validation-report~1]
    // [impl->dsn~rd-creates-release-report~1]
    /**
     * Main entry point for all Release Droid's calls.
     *
     * @param userInput configuration and command line arguments provided by the user
     */
    public void run(final UserInput userInput) {
        validateUserInput(userInput);
        final Repository repository = this.repositoryGateway.getRepository(userInput);

        final ReleasePlatforms platforms = ReleasePlatforms.from(userInput, repository);
        LOGGER.fine(() -> "Release Droid has received '" + userInput.getGoal() + "' request for the project '"
                + userInput.getFullRepositoryName() + "'.");
        final List<Report> reports = new ArrayList<>();
        final UseCase useCase = userInput.getGoal() == Goal.RELEASE ? this.releaseUseCase : this.validateUseCase;
        reports.addAll(useCase.apply(repository, platforms));
        processResponse(createResponse(reports, userInput, platforms.list()));
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