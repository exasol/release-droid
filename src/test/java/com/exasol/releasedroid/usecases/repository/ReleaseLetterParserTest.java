package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ReleaseLetterParserTest {
    @Test
    void testCreateNewReleaseChangesLetter() {
        final String content = "# Exasol Release Droid 0.1.0, released 2020-08-20 " + LINE_SEPARATOR
                + "Code name: GitHub validation and release support " + LINE_SEPARATOR //
                + "## Features " + LINE_SEPARATOR //
                + "* #3: Added initial implementation og GitHub Release. " + LINE_SEPARATOR
                + "* #10: Added support for validation on a user-specified git branch. " + LINE_SEPARATOR;
        final ReleaseLetter letter = new ReleaseLetterParser("name", content).parse();
        assertAll(() -> assertThat(letter.getFileName(), equalTo("name")),
                () -> assertThat(letter.getVersionNumber().get(), equalTo("0.1.0")),
                () -> assertThat(letter.getHeader().get(), equalTo("GitHub validation and release support")),
                () -> assertThat(letter.getReleaseDate().get(), equalTo(LocalDate.parse("2020-08-20"))),
                () -> assertThat(letter.getTicketNumbers(), containsInAnyOrder(3, 10)),
                () -> assertThat(letter.getBody().get(), startsWith("## Features")));
    }

    @Test
    void testCreateNewReleaseChangesLetterEmpty() {
        final String content = "short content";
        final ReleaseLetter letter = new ReleaseLetterParser("name", content).parse();
        assertAll(() -> assertThat(letter.getFileName(), equalTo("name")),
                () -> assertThat(letter.getVersionNumber().isEmpty(), equalTo(true)),
                () -> assertThat(letter.getHeader().isEmpty(), equalTo(true)),
                () -> assertThat(letter.getReleaseDate().isEmpty(), equalTo(true)),
                () -> assertThat(letter.getTicketNumbers().isEmpty(), equalTo(true)),
                () -> assertThat(letter.getBody().isEmpty(), equalTo(true)));
    }

    @Test
    void testParsingEmptyCodeName() {
        final String content = "# Exasol Release Droid 0.1.0, released 2020-08-20 " + LINE_SEPARATOR //
                + "Code name:      " + LINE_SEPARATOR //
                + "## Features ";
        final ReleaseLetter letter = new ReleaseLetterParser("name", content).parse();
        assertThat(letter.getHeader().isEmpty(), equalTo(true));
    }
}