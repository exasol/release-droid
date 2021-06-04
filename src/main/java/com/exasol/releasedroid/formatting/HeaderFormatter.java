package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import com.exasol.releasedroid.usecases.response.ReleaseDroidResponse;

/**
 * Formatter for the Release Droid response header.
 */
public class HeaderFormatter {
    /**
     * Format header string.
     *
     * @param response response
     * @return formatted header
     */
    public String formatHeader(final ReleaseDroidResponse response) {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        final var stringBuilder = new StringBuilder();
        stringBuilder.append(println(now));
        stringBuilder.append(println(""));
        stringBuilder.append(println("Goal: " + response.getGoal()));
        stringBuilder.append(println("Repository: " + response.getFullRepositoryName()));
        stringBuilder.append(println("Platforms: "
                + response.getPlatformNames().stream().map(Enum::name).collect(Collectors.joining(", "))));
        if (response.getBranch() != null) {
            stringBuilder.append(println("Git branch: " + response.getBranch()));
        }
        if (response.getLocalRepositoryPath() != null) {
            stringBuilder.append(println("Local path: " + response.getLocalRepositoryPath()));
        }
        stringBuilder.append(println(""));
        return stringBuilder.toString();
    }

    private String println(final String string) {
        return string + LINE_SEPARATOR;
    }
}