package com.exasol.releasedroid.adapter.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.exasol.releasedroid.usecases.repository.version.Version;
import com.exasol.releasedroid.usecases.repository.version.Version.VersionFormatException;

import nl.jqno.equalsverifier.EqualsVerifier;

class VersionTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource(value = { "a1.2.3", "1.a.2", "1.2", "1.", "1.2.3.4" })
    void illegalVersionFormat(final String version) {
        assertThrows(VersionFormatException.class, () -> Version.parse(version));
    }

    @Test
    void illegalGitTag() {
        final Exception e = assertThrows(VersionFormatException.class, () -> Version.fromGitTag("abc/1.2.3"));
        assertThat(e.getMessage(), containsString("led reading version from git tag"));
    }

    @Test
    void prefix() {
        assertThat(Version.parse("v1.2.3"), equalTo(new Version("", "v", 1, 2, 3)));
    }

    @Test
    void fromGitTag() {
        assertThat(Version.fromGitTag("refs/tags/v1.2.3"), equalTo(new Version("", "v", 1, 2, 3)));
        assertThat(Version.fromGitTag("refs/tags/go-module/v1.2.3"), equalTo(new Version("go-module/", "v", 1, 2, 3)));
    }

    @Test
    void testToString() {
        assertThat(Version.parse("v1.2.3").toString(), equalTo("v1.2.3"));
    }

    @Test
    void equalsContract() {
        EqualsVerifier.simple().forClass(Version.class).verify();
    }

    @Test
    void successors() {
        assertThat(Version.parse("v1.2.3").potentialSuccessors(), //
                containsInAnyOrder( //
                        Version.parse("2.0.0"), //
                        Version.parse("1.3.0"), //
                        Version.parse("1.2.4") //
                ));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(value = { "2.0.0", "1.3.0", "1.2.4" })
    void acceptSuccessors(final String other) {
        final Version version = Version.parse("1.2.3");
        assertThat(version.acceptsSuccessor(Version.parse(other)), is(true));
        assertThat(version.acceptsSuccessor(Version.parse("v" + other)), is(true));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(value = { "1.2.2", "2.1.2", "2.2.1", "2.2.2", "4.2.2", "2.4.2", "2.2.4" })
    void rejectSuccessors(final String other) {
        final Version version = Version.parse("2.2.2");
        assertThat(version.acceptsSuccessor(Version.parse(other)), is(false));
        assertThat(version.acceptsSuccessor(Version.parse("v" + other)), is(false));
    }

    @Test
    void sort() {
        final Stream<String> unsorted = Stream.of( //
                "v1.2.4", //
                "1.3.0", //
                "2.0.0", //
                "1.2.3", //
                "v1.2.3", //
                "v1.3.0", //
                "v2.0.0", //
                "1.2.3", //
                "1.2.4", //
                "v1.2.3");
        final Stream<String> sorted = Stream.of( //
                "1.2.3", //
                "1.2.3", //
                "v1.2.3", //
                "v1.2.3", //
                "1.2.4", //
                "v1.2.4", //
                "1.3.0", //
                "v1.3.0", //
                "2.0.0", //
                "v2.0.0");
        final List<Version> actual = unsorted //
                .map(Version::parse) //
                .sorted() //
                .collect(Collectors.toList());
        final List<Version> expected = sorted //
                .map(Version::parse) //
                .collect(Collectors.toList());
        assertEquals(actual, expected);
    }

}
