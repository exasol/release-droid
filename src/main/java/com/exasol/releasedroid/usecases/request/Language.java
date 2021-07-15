package com.exasol.releasedroid.usecases.request;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.exasol.errorreporting.ExaError;

/**
 * List of programming languages supported by release droid.
 */
public enum Language {
    JAVA, SCALA, GENERIC;

    /**
     * Get a {@link Language} from a string.
     *
     * @param languageAsString language as a string
     * @return member of {@link Language} enum class
     */
    public static Language getLanguage(final String languageAsString) {
        try {
            return Language.valueOf(languageAsString.toUpperCase().trim());
        } catch (final IllegalArgumentException exception) {
            final List<String> allowedLanguages = Arrays.stream(Language.values())
                    .map(language -> language.toString().toLowerCase()).collect(Collectors.toList());
            throw new IllegalArgumentException(
                    ExaError.messageBuilder("E-RD-8").message("Cannot parse a language {{languageAsString}}.") //
                            .parameter("languageAsString", languageAsString)
                            .mitigation("Please, use one of the following languages: {{allowedLanguages}}.")
                            .parameter("allowedLanguages", String.join(",", allowedLanguages)).toString(),
                    exception);
        }
    }
}