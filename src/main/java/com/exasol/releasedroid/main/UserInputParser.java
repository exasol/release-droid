package com.exasol.releasedroid.main;

import org.apache.commons.cli.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.UserInput;

/**
 * Parser for user input.
 */
public class UserInputParser {
    private static final String REPOSITORY_OWNER = "exasol";
    private static final String PLATFORM_SHORT_OPTION = "p";
    private static final String NAME_SHORT_OPTION = "n";
    private static final String GOAL_SHORT_OPTION = "g";
    private static final String BRANCH_SHORT_OPTION = "b";

    /**
     * Parse user input.
     * 
     * @param args user input from the console
     * @return instance of {@link UserInput}
     */
    public UserInput parseUserInput(final String[] args) {
        final Options options = createOptions();
        final CommandLine cmd = getCommandLine(args, options);
        return UserInput.builder() //
                .repositoryName(REPOSITORY_OWNER + "/" + cmd.getOptionValue(NAME_SHORT_OPTION)) //
                .goal(cmd.getOptionValue(GOAL_SHORT_OPTION)) //
                .platforms(cmd.getOptionValues(PLATFORM_SHORT_OPTION)) //
                .branch(cmd.getOptionValue(BRANCH_SHORT_OPTION)) //
                .build();
    }

    private static Options createOptions() {
        final Option name = new Option(NAME_SHORT_OPTION, "name", true, "project name");
        name.setRequired(true);
        final Option goal = new Option(GOAL_SHORT_OPTION, "goal", true, "goal to execute");
        goal.setRequired(true);
        final Option platforms = new Option(PLATFORM_SHORT_OPTION, "platforms", true,
                "comma-separated list of release platforms");
        platforms.setRequired(true);
        platforms.setArgs(Option.UNLIMITED_VALUES);
        platforms.setValueSeparator(',');
        final Option branch = new Option(BRANCH_SHORT_OPTION, "branch", true, "git branch (only for validation)");
        return new Options().addOption(name).addOption(goal).addOption(platforms).addOption(branch);
    }

    private static CommandLine getCommandLine(final String[] args, final Options options) {
        try {
            final CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        } catch (final ParseException exception) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Release Droid", options);
            throw new IllegalArgumentException(ExaError.messageBuilder("E-RR-RUN-1").message("{{cause}}")
                    .unquotedParameter("cause", exception.getMessage()).toString());
        }
    }
}