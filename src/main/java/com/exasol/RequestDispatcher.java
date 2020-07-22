package com.exasol;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exasol.github.*;
import com.exasol.release.ReleasePlatform;

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
     * @param goalAsString run goal. Supported goals: release, validate
     * @param platforms one or more platforms for validation or release. Supported values: github
     */
    public void dispatch(final String repositoryName, final String goalAsString, final String... platforms) {
        LOGGER.info("Release Robot has received '{}' request for the project '{}'.", goalAsString, repositoryName);
        try {
            final Goal goal = getGoal(goalAsString);
            final Set<ReleasePlatform> platformsList = getReleasePlatformsList(platforms);
            final GitHubRepository repository = GitHubRepositoryFactory.getInstance()
                    .createGitHubRepository(REPOSITORY_OWNER, repositoryName);
            final RepositoryHandler repositoryHandler = new RepositoryHandler(repository, platformsList);
            if (goal == Goal.VALIDATE) {
                repositoryHandler.validate();
            } else {
                repositoryHandler.validate();
                repositoryHandler.release();
            }
        } catch (final RuntimeException exception) {
            LOGGER.error("'{}' request failed. Cause: {}", goalAsString, exception.getMessage());
        }
    }

    private Goal getGoal(final String goalAsString) {
        try {
            return Goal.valueOf(goalAsString.toUpperCase());
        } catch (final IllegalArgumentException illegalArgumentException) {
            final List<String> allowedGoals = Arrays.stream(Goal.values()).map(goal -> goal.toString().toLowerCase())
                    .collect(Collectors.toList());
            LOGGER.error("Cannot parse a goal '{}'. Please, use one of the following goals: {}", goalAsString,
                    String.join(",", allowedGoals));
            throw illegalArgumentException;
        }
    }

    protected Set<ReleasePlatform> getReleasePlatformsList(final String[] platforms) {
        final Set<ReleasePlatform> platformsList = new HashSet<>();
        for (final String platform : platforms) {
            platformsList.add(ReleasePlatform.valueOf(platform.toUpperCase()));
        }
        return platformsList;
    }

    /**
     * Run the Release Robot.
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