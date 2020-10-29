package com.exasol.releaserobot.main;

import static com.exasol.releaserobot.Platform.PlatformName.GITHUB;
import static com.exasol.releaserobot.Platform.PlatformName.MAVEN;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.exasol.releaserobot.Platform;
import com.exasol.releaserobot.Platform.PlatformName;
import com.exasol.releaserobot.ReleaseRobot;
import com.exasol.releaserobot.RepositoryHandler;
import com.exasol.releaserobot.UserInput;
import com.exasol.releaserobot.github.GitHubEntityFactory;
import com.exasol.releaserobot.repository.GitRepository;

/**
 * This class contains main method.
 */
public class Runner {
    private static final String REPOSITORY_OWNER = "exasol";
    private static final String PLATFORM_SHORT_OPTION = "p";
    private static final String NAME_SHORT_OPTION = "n";
    private static final String GOAL_SHORT_OPTION = "g";
    private static final String BRANCH_SHORT_OPTION = "b";

    /**
     * Run the Release Robot.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
    	
        final Options options = createOptions();
        final CommandLine cmd = getCommandLine(args, options);
        final UserInput userInput = UserInput.builder() //
                .repositoryName(cmd.getOptionValue(NAME_SHORT_OPTION)) //
                .goal(cmd.getOptionValue(GOAL_SHORT_OPTION)) //
                .platforms(cmd.getOptionValue(PLATFORM_SHORT_OPTION).split(",")) //
                .gitBranch(cmd.getOptionValue(BRANCH_SHORT_OPTION)) //
                .repositoryOwner(REPOSITORY_OWNER) //
                .build();
        
        final RepositoryHandler repositoryHandler = createRepositoryHandler(userInput);
        new ReleaseRobot(repositoryHandler).run(userInput);
    }
    
    private static RepositoryHandler createRepositoryHandler(final UserInput userInput) {
        final GitHubEntityFactory gitHubEntityFactory = new GitHubEntityFactory(userInput.getRepositoryOwner(),
                userInput.getRepositoryName());
        final GitRepository repository = gitHubEntityFactory.createGitHubGitRepository();
        final Set<Platform> platforms = createPlatforms(userInput, gitHubEntityFactory);
        return new RepositoryHandler(repository, platforms);
    }

    private static Set<Platform> createPlatforms(final UserInput userInput, final GitHubEntityFactory gitHubEntityFactory) {
        final Set<Platform> platforms = new HashSet<>();
        for (final PlatformName name : userInput.getPlatformNames()) {
            if (name == GITHUB) {
                platforms.add(gitHubEntityFactory.createGitHubPlatform());
            } else if (name == MAVEN) {
                platforms.add(gitHubEntityFactory.createMavenPlatform());
            }
        }
        return platforms;
    }
    
    private static Options createOptions() {
        final Option name = new Option(NAME_SHORT_OPTION, "name", true, "project name");
        name.setRequired(true);
        final Option goal = new Option(GOAL_SHORT_OPTION, "goal", true, "goal to execute");
        goal.setRequired(true);
        final Option platforms = new Option(PLATFORM_SHORT_OPTION, "platforms", true,
                "comma-separated list of release platforms");
        platforms.setRequired(true);
        final Option branch = new Option(BRANCH_SHORT_OPTION, "branch", true, "git branch (only for validation)");
        return new Options().addOption(name).addOption(goal).addOption(platforms).addOption(branch);
    }

    private static CommandLine getCommandLine(final String[] args, final Options options) {
        try {
            final CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (final ParseException exception) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Release Robot", options);
            throw new IllegalArgumentException("E-RR-RUN-1" + exception.getMessage());
        }
    }
}