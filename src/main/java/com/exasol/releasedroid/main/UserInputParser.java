package com.exasol.releasedroid.main;

import org.apache.commons.cli.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.request.UserInput;

/**
 * Parser for user input.
 */
public class UserInputParser {
    private static final Option NAME = new Option("n", "name", true, "project name");
    private static final Option GOAL = new Option("g", "goal", true, "goal to execute");
    private static final Option PLATFORM = Option.builder().option("p").longOpt("platforms") //
            .numberOfArgs(Option.UNLIMITED_VALUES).valueSeparator(',') //
            .desc("comma-separated list of release platforms") //
            .build();
    private static final Option BRANCH = new Option("b", "branch", true, "git branch (only for validation)");
    private static final Option LOCAL = new Option("l", "local", true, "local path to the repository");
    private static final Option LANGUAGE = new Option("lg", "language", true, //
            "programming language of the repository");
    private static final Option HELP = new Option("h", "help", false, "Help command");
    private static final Option SKIP_VALIDATION = new Option("skipvalidation", "skipvalidation", false,
            "Release without validation.");
    private static final Option RELEASE_GUIDE = new Option("guide", "release-guide", true, "Generate release guide.");

    /**
     * Parse user input.
     *
     * @param args user input from the console
     * @return instance of {@link UserInput}
     */
    public UserInput parseUserInput(final String[] args) {
        final Options options = createOptions();
        final CommandLine cmd = getCommandLine(args, options);
        printHelpIfNeeded(options, cmd);
        return UserInput.builder() //
                .repositoryName(cmd.getOptionValue(NAME)) //
                .goal(cmd.getOptionValue(GOAL)) //
                .platforms(cmd.getOptionValues(PLATFORM)) //
                .branch(cmd.getOptionValue(BRANCH)) //
                .localPath(cmd.getOptionValue(LOCAL)) //
                .language(cmd.getOptionValue(LANGUAGE)) //
                .skipValidation(cmd.hasOption(SKIP_VALIDATION)) //
                .releaseGuide(cmd.getOptionValue(RELEASE_GUIDE)) //
                .build();
    }

    private void printHelpIfNeeded(final Options options, final CommandLine cmd) {
        if (cmd.hasOption(HELP)) {
            printHelp(options);
            System.exit(0);
        }
    }

    private static Options createOptions() {
        return new Options() //
                .addOption(NAME) //
                .addOption(GOAL) //
                .addOption(PLATFORM) //
                .addOption(BRANCH) //
                .addOption(LOCAL) //
                .addOption(LANGUAGE) //
                .addOption(HELP) //
                .addOption(SKIP_VALIDATION) //
                .addOption(RELEASE_GUIDE);
    }

    private static CommandLine getCommandLine(final String[] args, final Options options) {
        try {
            final CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (final ParseException exception) {
            printHelp(options);
            throw new IllegalArgumentException(
                    ExaError.messageBuilder("E-RD-9").message("{{cause|uq}}", exception.getMessage()).toString());
        }
    }

    private static void printHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Release Droid", options);
    }
}
