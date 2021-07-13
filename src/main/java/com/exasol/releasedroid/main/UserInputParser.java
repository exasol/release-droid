package com.exasol.releasedroid.main;

import org.apache.commons.cli.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.request.UserInput;

/**
 * Parser for user input.
 */
public class UserInputParser {
    private static final String PLATFORM_SHORT_OPTION = "p";
    private static final String NAME_SHORT_OPTION = "n";
    private static final String GOAL_SHORT_OPTION = "g";
    private static final String BRANCH_SHORT_OPTION = "b";
    private static final String LOCAL_SHORT_OPTION = "l";
    private static final String LANGUAGE_SHORT_OPTION = "lg";
    private static final String HELP_SHORT_OPTION = "h";
    private static final String SKIP_VALIDATION_OPTION = "skipvalidation";

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
                .repositoryName(cmd.getOptionValue(NAME_SHORT_OPTION)) //
                .goal(cmd.getOptionValue(GOAL_SHORT_OPTION)) //
                .platforms(cmd.getOptionValues(PLATFORM_SHORT_OPTION)) //
                .branch(cmd.getOptionValue(BRANCH_SHORT_OPTION)) //
                .localPath(cmd.getOptionValue(LOCAL_SHORT_OPTION)) //
                .language(cmd.getOptionValue(LANGUAGE_SHORT_OPTION)) //
                .skipValidation(cmd.hasOption(SKIP_VALIDATION_OPTION)).build();
    }

    private void printHelpIfNeeded(final Options options, final CommandLine cmd) {
        if (cmd.hasOption(HELP_SHORT_OPTION)) {
            printHelp(options);
            System.exit(0);
        }
    }

    private static Options createOptions() {
        final Option name = new Option(NAME_SHORT_OPTION, "name", true, "project name");
        final Option goal = new Option(GOAL_SHORT_OPTION, "goal", true, "goal to execute");
        final Option platforms = new Option(PLATFORM_SHORT_OPTION, "platforms", true,
                "comma-separated list of release platforms");
        platforms.setArgs(Option.UNLIMITED_VALUES);
        platforms.setValueSeparator(',');
        final Option branch = new Option(BRANCH_SHORT_OPTION, "branch", true, "git branch (only for validation)");
        final Option local = new Option(LOCAL_SHORT_OPTION, "local", true, "local path to the repository");
        final Option language = new Option(LANGUAGE_SHORT_OPTION, "language", true,
                "programming language of the repository");
        final Option help = new Option(HELP_SHORT_OPTION, "help", false, "Help command");
        final Option skipValidation = new Option(SKIP_VALIDATION_OPTION, SKIP_VALIDATION_OPTION, false,
                "Release without validation.");
        return new Options().addOption(name).addOption(goal).addOption(platforms).addOption(branch).addOption(local)
                .addOption(language).addOption(help).addOption(skipValidation);
    }

    private static CommandLine getCommandLine(final String[] args, final Options options) {
        try {
            final CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (final ParseException exception) {
            printHelp(options);
            throw new IllegalArgumentException(ExaError.messageBuilder("E-RD-9").message("{{cause}}")
                    .unquotedParameter("cause", exception.getMessage()).toString());
        }
    }

    private static void printHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Release Droid", options);
    }
}