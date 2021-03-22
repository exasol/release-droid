package com.exasol.releasedroid.usecases.request;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.exasol.errorreporting.ExaError;

/**
 * This class contains goals Release Droid supports.
 */
public enum Goal {
    VALIDATE, RELEASE;

    /**
     * Get a {@link Goal} from a string.
     * 
     * @param goalAsString goal as a string
     * @return member of {@link Goal} enum class
     */
    public static Goal getGoal(final String goalAsString) {
        try {
            return Goal.valueOf(goalAsString.toUpperCase().trim());
        } catch (final IllegalArgumentException exception) {
            final List<String> allowedGoals = Arrays.stream(Goal.values()).map(goal -> goal.toString().toLowerCase())
                    .collect(Collectors.toList());
            throw new IllegalArgumentException(
                    ExaError.messageBuilder("E-RR-7").message("Cannot parse a goal {{goalAsString}}.") //
                            .parameter("goalAsString", goalAsString)
                            .mitigation("Please, use one of the following goals: {{allowedGoals}}.")
                            .parameter("allowedGoals", String.join(",", allowedGoals)).toString(),
                    exception);
        }
    }
}