package com.exasol;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains goals Release Robot supports.
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
                    MessageFormat.format("E-G-1: Cannot parse a goal '{}'. Please, use one of the following goals: {}",
                            goalAsString, String.join(",", allowedGoals)),
                    exception);
        }
    }
}