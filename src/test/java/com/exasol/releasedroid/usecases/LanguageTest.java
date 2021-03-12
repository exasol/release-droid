package com.exasol.releasedroid.usecases;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LanguageTest {
    @ParameterizedTest
    @ValueSource(strings = { "java  ", " scala" })
    void testGetLanguageValid(final String language) {
        assertThat(Language.getLanguage(language), in(Language.values()));
    }

    @ParameterizedTest
    @ValueSource(strings = { "python", " c++" })
    void testGetLanguageInvalid(final String language) {
        assertThrows(IllegalArgumentException.class, () -> Language.getLanguage(language));
    }
}