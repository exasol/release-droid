package com.exasol;

import static com.exasol.Platform.PlatformName.GITHUB;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.exasol.Platform.PlatformName;
import com.exasol.github.GitHubEntityFactory;
import com.exasol.repository.GitRepository;
import com.exasol.validation.ValidationReport;

/**
 * This class is the main entry point for calls to a Release Robot.
 */
public class ReleaseRobot {
    private static final Logger LOGGER = Logger.getLogger(ReleaseRobot.class.getName());
    private final UserInput userInput;

    public ReleaseRobot(final UserInput userInput) {
        this.userInput = userInput;
    }

    /**
     * Main entry point for all Release Robot's calls.
     */
    // [impl->dsn~rr-starts-release-only-if-all-validation-succeed~1]
    public void run() {
        LOGGER.fine(() -> "Release Robot has received '" + this.userInput.getGoal() + "' request for the project "
                + this.userInput.getRepositoryName() + ".");
        final RepositoryHandler repositoryHandler = createRepositoryHandler();
        final ValidationReport validationReport = runValidation(repositoryHandler);
        if (validationReport.hasFailedValidations()) {
            logFailedValidation(validationReport);
        } else {
            runReleaseIfNeeded(repositoryHandler);
        }
        final ReportWriter reportWriter = new ReportWriter(this.userInput);
        reportWriter.writeValidationReportToFile(validationReport);
    }

    private RepositoryHandler createRepositoryHandler() {
        final GitHubEntityFactory gitHubEntityFactory = new GitHubEntityFactory(this.userInput.getRepositoryOwner(),
                this.userInput.getRepositoryName());
        final GitRepository repository = gitHubEntityFactory.createGitHubGitRepository();
        final Set<Platform> platforms = createPlatforms(gitHubEntityFactory);
        return new RepositoryHandler(repository, platforms);
    }

    private Set<Platform> createPlatforms(final GitHubEntityFactory gitHubEntityFactory) {
        final Set<Platform> platforms = new HashSet<>();
        for (final PlatformName name : this.userInput.getPlatformNames()) {
            if (name == GITHUB) {
                final Platform gitHubPlatform = gitHubEntityFactory.createGitHubPlatform();
                platforms.add(gitHubPlatform);
            }
        }
        return platforms;
    }

    // [impl->dsn~rr-runs-validate-goal~1]
    private ValidationReport runValidation(final RepositoryHandler repositoryHandler) {
        if (validateUserSpecifiedBranch()) {
            return repositoryHandler.validate(this.userInput.getGitBranch());
        } else {
            return repositoryHandler.validate();
        }
    }

    private void logFailedValidation(final ValidationReport validationReport) {
        LOGGER.severe(() -> "'" + this.userInput.getGoal() + "' request failed. Validation report: "
                + validationReport.getFailedValidations());
    }

    private boolean validateUserSpecifiedBranch() {
        if (this.userInput.getGoal() == Goal.RELEASE) {
            return false;
        } else {
            return this.userInput.hasGitBranch();
        }
    }

    // [impl->dsn~rr-runs-release-goal~1]
    private void runReleaseIfNeeded(final RepositoryHandler repositoryHandler) {
        if (this.userInput.getGoal() == Goal.RELEASE) {
            repositoryHandler.release();
        }
    }
}