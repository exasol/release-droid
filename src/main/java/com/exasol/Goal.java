package com.exasol;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains goals Release Robot supports.
 */
public enum Goal {
    VALIDATE, RELEASE;

    private static final Logger LOGGER = LoggerFactory.getLogger(Goal.class);

    /**
     * Get a {@link Goal} from a string.
     * 
     * @param goalAsString goal as a string
     * @return member of {@link Goal} enum class
     */
    public static Goal getGoal(final String goalAsString) {
        try {
            return Goal.valueOf(goalAsString.toUpperCase().trim());
        } catch (final IllegalArgumentException illegalArgumentException) {
            final List<String> allowedGoals = Arrays.stream(Goal.values()).map(goal -> goal.toString().toLowerCase())
                    .collect(Collectors.toList());
            LOGGER.error("Cannot parse a goal '{}'. Please, use one of the following goals: {}", goalAsString,
                    String.join(",", allowedGoals));
            throw illegalArgumentException;
        }
    }
}