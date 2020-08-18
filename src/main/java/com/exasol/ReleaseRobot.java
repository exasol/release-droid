package com.exasol;

import java.text.MessageFormat;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.cli.*;

import com.exasol.github.*;

/**
 * This class is the main entry point for calls to a Release Robot.
 */
public class ReleaseRobot {
    private static final Logger LOGGER = Logger.getLogger(ReleaseRobot.class.getName());
    private static final String REPOSITORY_OWNER = "exasol";
    private static final String PLATFORM_SHORT_OPTION = "p";
    private static final String NAME_SHORT_OPTION = "n";
    private static final String GOAL_SHORT_OPTION = "g";

    /**
     * Main entry point for all Release Robot's calls.
     * 
     * @param repositoryName name of a target project from GitHub
     * @param goalAsString run goal. Supported goals: release, validate
     * @param platforms one or more platforms for validation or release. Supported values: github
     */
    public void dispatch(final String repositoryName, final String goalAsString, final String... platforms) {
        LOGGER.fine(MessageFormat.format("Release Robot has received '{}' request for the project '{}'.", goalAsString,
                repositoryName));
        try {
            final Goal goal = Goal.getGoal(goalAsString);
            final RepositoryHandler repositoryHandler = getRepositoryHandler(repositoryName, platforms);
            if (goal == Goal.VALIDATE) {
                repositoryHandler.validate();
            } else {
                repositoryHandler.validate();
                repositoryHandler.release();
            }
        } catch (final RuntimeException exception) {
            LOGGER.severe(MessageFormat.format("'{}' request failed. Cause: {}", goalAsString, exception.getMessage()));
        }
    }

    private RepositoryHandler getRepositoryHandler(final String repositoryName, final String[] platforms) {
        final Set<ReleasePlatform> platformsList = ReleasePlatform.toSet(platforms);
        final CredentialsProvider credentialsProvider = CredentialsProvider.getInstance();
        final GitHubRepository repository = GitHubRepositoryFactory.getInstance().createGitHubRepository(
                REPOSITORY_OWNER, repositoryName, credentialsProvider.provideGitHubCredentials());
        return new RepositoryHandler(repository, platformsList);
    }

    /**
     * Run the Release Robot.
     * 
     * @param args arguments
     */
    public static void main(final String[] args) {
        final Options options = createOptions();
        final CommandLine cmd = getCommandLine(args, options);
        final String[] platformsArray = cmd.getOptionValue(PLATFORM_SHORT_OPTION).split(",");
        final ReleaseRobot releaseRobot = new ReleaseRobot();
        releaseRobot.dispatch(cmd.getOptionValue(NAME_SHORT_OPTION), cmd.getOptionValue(GOAL_SHORT_OPTION),
                platformsArray);
    }

    private static Options createOptions() {
        final Option name = new Option(NAME_SHORT_OPTION, "name", true, "project name");
        name.setRequired(true);
        final Option goal = new Option(GOAL_SHORT_OPTION, "goal", true, "goal to execute");
        goal.setRequired(true);
        final Option platforms = new Option(PLATFORM_SHORT_OPTION, "platforms", true,
                "comma-separated list of release platforms");
        platforms.setRequired(true);
        return new Options().addOption(name).addOption(goal).addOption(platforms);
    }

    private static CommandLine getCommandLine(final String[] args, final Options options) {
        try {
            final CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (final ParseException exception) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Release Robot", options);
            throw new IllegalArgumentException(exception.getMessage());
        }
    }
}
