package com.exasol.release.robot;

import static com.exasol.release.robot.Platform.PlatformName.GITHUB;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.release.robot.Platform.PlatformName;
import com.exasol.release.robot.github.GitHubEntityFactory;
import com.exasol.release.robot.report.*;
import com.exasol.release.robot.repository.GitRepository;

/**
 * This class is the main entry point for calls to a Release Robot.
 */
public class ReleaseRobot {
    private static final Logger LOGGER = Logger.getLogger(ReleaseRobot.class.getName());
    private static final String HOME_DIRECTORY = System.getProperty("user.home");
    private static final Path REPORT_PATH = Paths.get(HOME_DIRECTORY, ".release-robot", "last_report.txt");
    private final UserInput userInput;

    public ReleaseRobot(final UserInput userInput) {
        this.userInput = userInput;
    }

    /**
     * Main entry point for all Release Robot's calls.
     */
    // [impl->dsn~rr-starts-release-only-if-all-validation-succeed~1]
    // [impl->dsn~rr-runs-release-goal~1]
    public void run() {
        LOGGER.fine(() -> "Release Robot has received '" + this.userInput.getGoal() + "' request for the project "
                + this.userInput.getRepositoryName() + ".");
        final RepositoryHandler repositoryHandler = createRepositoryHandler();
        final List<Report> reports = new ArrayList<>();
        final ValidationReport validationReport = runValidation(repositoryHandler);
        reports.add(validationReport);
        logResults(validationReport);
        if (this.userInput.getGoal() == Goal.RELEASE && !validationReport.hasFailures()) {
            final ReleaseReport releaseReport = repositoryHandler.release();
            reports.add(releaseReport);
            logResults(releaseReport);
        }
        new ReportWriter(this.userInput, REPORT_PATH).writeValidationReportToFile(reports);
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

    private boolean validateUserSpecifiedBranch() {
        if (this.userInput.getGoal() == Goal.RELEASE) {
            return false;
        } else {
            return this.userInput.hasGitBranch();
        }
    }

    private void logResults(final Report report) {
        if (report.hasFailures()) {
            LOGGER.severe(() -> "'" + this.userInput.getGoal() + "' request failed: " + report.getFailuresReport());
        } else {
            LOGGER.info(report.getShortDescription());
        }
    }
}