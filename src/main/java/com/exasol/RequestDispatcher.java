package com.exasol;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.platform.GitHubRepository;
import com.exasol.platform.RepositoryHandlerFactory;

/**
 * This class is the main entry point for calls to a Release Robot.
 */
public class RequestDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestDispatcher.class);
    private static final String REPOSITORY_OWNER = "exasol";

    /**
     * Main entry point for all Release Robot's calls.
     * 
     * @param repositoryName name of a target project from GitHub
     * @param goal run goal. Supported goals: release, validate
     * @param platforms one or more platforms for validation or release. Supported values: github
     */
    public void dispatch(final String repositoryName, final String goal, final String... platforms) {
        LOGGER.info("Release Robot has received '{}' request for the project '{}'.", goal, repositoryName);
        try {
            final Set<ReleasePlatform> platformsList = getReleasePlatformsList(platforms);
            if (goal.equalsIgnoreCase("validate")) {
                runValidate(repositoryName, platformsList);
            } else if (goal.equalsIgnoreCase("release")) {
                runRelease(repositoryName, platformsList);
            } else {
                throw new UnsupportedOperationException(
                        "'" + goal + "' goal is unknown. Please, use one of the following goals: release, validate");
            }
        } catch (final RuntimeException exception) {
            LOGGER.error("'{}' request failed. Cause: {}", goal, exception.getMessage());
        }
    }

    private void runValidate(final String repositoryName, final Set<ReleasePlatform> platformsList) {
        final GitHubRepository repository = GitHubRepository.getAnonymousGitHubRepository(REPOSITORY_OWNER,
                repositoryName);
        final RepositoryHandler repositoryHandler = RepositoryHandlerFactory.getReleaseHandler(repository,
                platformsList);
        repositoryHandler.validate();
    }

    private void runRelease(final String repositoryName, final Set<ReleasePlatform> platformsList) {
        final GitHubRepository repository = GitHubRepository.getLogInGitHubRepository(REPOSITORY_OWNER, repositoryName);
        final RepositoryHandler repositoryHandler = RepositoryHandlerFactory.getReleaseHandler(repository,
                platformsList);
        repositoryHandler.validate();
        repositoryHandler.release();
    }

    protected Set<ReleasePlatform> getReleasePlatformsList(final String[] platforms) {
        final Set<ReleasePlatform> platformsList = new HashSet<>();
        for (final String platform : platforms) {
            platformsList.add(ReleasePlatform.valueOf(platform.toUpperCase()));
        }
        return platformsList;
    }

    /**
     * Run the Release Robot from the terminal.
     * 
     * @param args arguments
     */
    public static void main(final String[] args) {
        final Option name = new Option("n", "name", true, "project name");
        name.setRequired(true);
        final Option goal = new Option("g", "goal", true, "goal to execute");
        goal.setRequired(true);
        final Option platforms = new Option("p", "platforms", true, "comma-separated list of release platforms");
        platforms.setRequired(true);
        final Options options = new Options().addOption(name).addOption(goal).addOption(platforms);
        final CommandLineParser parser = new DefaultParser();
        final HelpFormatter formatter = new HelpFormatter();
        try {
            final CommandLine cmd = parser.parse(options, args);
            final String[] platformsArray = cmd.getOptionValue("p").split(",");
            final RequestDispatcher requestDispatcher = new RequestDispatcher();
            requestDispatcher.dispatch(cmd.getOptionValue("n"), cmd.getOptionValue("g"), platformsArray);
        } catch (final ParseException exception) {
            LOGGER.error(exception.getMessage());
            formatter.printHelp("Release Robot", options);
        }
    }
}