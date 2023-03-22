package com.exasol.releasedroid.main;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.output.guide.ReleaseGuide;
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

    private RepositoryGateway repositoryGateway;
    private UseCase releaseUseCase;
    private UseCase validateUseCase;
    private ReleaseDroidResponseConsumer loggerResponseConsumer;
    private ReleaseDroidResponseConsumer diskWriterResponseConsumer;

    private ReleaseDroid() {
        // use builder
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

        final Optional<Path> releaseGuide = userInput.releaseGuide();
        if (releaseGuide.isPresent()) {
            ReleaseGuide.from(repository).write(releaseGuide.get());
        }
        if (needsValidation(userInput)) {
            reports.addAll(report(this.validateUseCase, userInput, repository, platforms));
        }
        // [impl->dsn~rd-starts-release-only-if-all-validations-succeed~1]
        if ((userInput.getGoal() == Goal.RELEASE) && !hasFailures(reports)) {
            reports.addAll(report(this.releaseUseCase, userInput, repository, platforms));
        }
        this.diskWriterResponseConsumer.consumeResponse(createResponse(reports, userInput, platforms.list()));
    }

    private List<Report> report(final UseCase useCase, final UserInput userInput, final Repository repository,
            final ReleasePlatforms platforms) {
        final List<Report> report = useCase.apply(repository, platforms);
        final ReleaseDroidResponse summary = createResponse(report, userInput, platforms.list());
        this.loggerResponseConsumer.consumeResponse(summary);
        return report;
    }

    private boolean needsValidation(final UserInput userInput) {
        if (userInput.getGoal() != Goal.RELEASE) {
            return true;
        }
        return !userInput.skipValidation();
    }

    private boolean hasFailures(final List<Report> reports) {
        return reports.stream().map(Report::hasFailures).findAny().orElse(false);
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
                .message("Please specify mandatory parameter {{parameter}} and re-run the Release Droid.", parameter)
                .toString());
    }

    static Builder builder() {
        return new Builder();
    }

    static final class Builder {
        private final ReleaseDroid result = new ReleaseDroid();

        private Builder() {
        }

        Builder repositoryGateway(final RepositoryGateway value) {
            this.result.repositoryGateway = value;
            return this;
        }

        Builder validateUseCase(final UseCase value) {
            this.result.validateUseCase = value;
            return this;
        }

        Builder releaseUseCase(final UseCase value) {
            this.result.releaseUseCase = value;
            return this;
        }

        Builder loggerResponseConsumer(final ReleaseDroidResponseConsumer value) {
            this.result.loggerResponseConsumer = value;
            return this;
        }

        Builder diskWriterResponseConsumer(final ReleaseDroidResponseConsumer value) {
            this.result.diskWriterResponseConsumer = value;
            return this;
        }

        ReleaseDroid build() {
            return this.result;
        }
    }
}