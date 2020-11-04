package com.exasol.releaserobot.main;

import java.util.*;

import org.apache.commons.cli.*;

import com.exasol.releaserobot.github.GitHubEntityFactory;
import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.GitRepository;
import com.exasol.releaserobot.usecases.validate.BasicMavenPomValidator;
import com.exasol.releaserobot.usecases.PlatformName;
import com.exasol.releaserobot.usecases.UserInput;
import com.exasol.releaserobot.usecases.release.*;
import com.exasol.releaserobot.usecases.validate.*;

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
    public static void main(final String[] args) throws GitHubException {
        final Options options = createOptions();
        final CommandLine cmd = getCommandLine(args, options);
        final UserInput userInput = UserInput.builder() //
                .repositoryName(cmd.getOptionValue(NAME_SHORT_OPTION)) //
                .goal(cmd.getOptionValue(GOAL_SHORT_OPTION)) //
                .platforms(cmd.getOptionValue(PLATFORM_SHORT_OPTION).split(",")) //
                .gitBranch(cmd.getOptionValue(BRANCH_SHORT_OPTION)) //
                .repositoryOwner(REPOSITORY_OWNER) //
                .build();
        createReleaseRobot(userInput).run(userInput);
    }

    private static ReleaseRobot createReleaseRobot(final UserInput userInput) throws GitHubException {
        final GitHubEntityFactory gitHubEntityFactory = new GitHubEntityFactory(userInput.getRepositoryOwner(),
                userInput.getRepositoryName());
        final GitRepository repository = gitHubEntityFactory.createGitHubGitRepository();
        final Map<PlatformName, ReleaseMaker> releaseMakers = createReleaseMakers(userInput, gitHubEntityFactory,
                repository);
        final List<PlatformValidator> platformValidators = createPlatformValidators(userInput, gitHubEntityFactory,
                repository);
        final List<RepositoryValidator> repositoryValidators = createRepositoryValidators(repository);
        final ValidateUseCase validateUseCase = new ValidateInteractor(platformValidators, repositoryValidators);
        final ReleaseUseCase releaseUseCase = new ReleaseInteractor(validateUseCase, releaseMakers);
        return new ReleaseRobot(releaseUseCase, validateUseCase);
    }

    private static List<RepositoryValidator> createRepositoryValidators(final GitRepository repository) {
        final List<RepositoryValidator> repositoryValidators = new ArrayList<>();
        repositoryValidators.add(new GitRepositoryValidator(repository));
        repositoryValidators.add(new BasicMavenPomValidator(repository));
        return repositoryValidators;
    }

    private static List<PlatformValidator> createPlatformValidators(final UserInput userInput,
            final GitHubEntityFactory gitHubEntityFactory, final GitRepository repository) {
        final List<PlatformValidator> platformValidators = new ArrayList<>();
        final GitBranchContent branchContent = getBranchContent(userInput, repository);
        for (final PlatformName name : userInput.getPlatformNames()) {
            switch (name) {
            case GITHUB:
                platformValidators.add(gitHubEntityFactory.createGithubPlatformValidator(branchContent));
                break;
            case MAVEN:
                platformValidators.add(gitHubEntityFactory.createMavenPlatformValidator(branchContent));
                break;
            default:
                throw new UnsupportedOperationException(
                        "E-RR-RUN-2: Platform '" + name + "' is not supported. Please choose one of: "
                                + PlatformName.availablePlatformNames().toString());
            }
        }
        return platformValidators;
    }

    private static GitBranchContent getBranchContent(final UserInput userInput, final GitRepository repository) {
        if (userInput.hasGitBranch()) {
            return repository.getRepositoryContent(userInput.getGitBranch());
        } else {
            return repository.getRepositoryContent(repository.getDefaultBranchName());
        }
    }

    private static Map<PlatformName, ReleaseMaker> createReleaseMakers(final UserInput userInput,
            final GitHubEntityFactory gitHubEntityFactory, final GitRepository repository) {
        final Map<PlatformName, ReleaseMaker> releaseMakers = new HashMap<>();
        final GitBranchContent defaultBranchContent = repository
                .getRepositoryContent(repository.getDefaultBranchName());
        for (final PlatformName name : userInput.getPlatformNames()) {
            switch (name) {
            case GITHUB:
                releaseMakers.put(name, gitHubEntityFactory.createGithubReleaseMaker(defaultBranchContent));
                break;
            case MAVEN:
                releaseMakers.put(name, gitHubEntityFactory.createMavenReleaseMaker(defaultBranchContent));
                break;
            default:
                throw new UnsupportedOperationException(
                        "E-RR-RUN-2: Platform '" + name + "' is not supported. Please choose one of: "
                                + PlatformName.availablePlatformNames().toString());
            }
        }
        return releaseMakers;
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
            throw new IllegalArgumentException("E-RR-RUN-1: " + exception.getMessage());
        }
    }
}