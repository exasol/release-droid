package com.exasol.releasedroid.adapter.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nl.jqno.equalsverifier.EqualsVerifier;

public class VersionTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource(value = { "a1.2.3", "1.a.2", "1.2", "1.", "1.2.3.4" })
    void illegalVersionFormat(final String version) {
        assertThrows(IllegalArgumentException.class, () -> Version.parse(version));
    }

    @Test
    void test() {
        assertThat(Version.parse("v1.2.3"), equalTo(new Version("v", 1, 2, 3)));
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
                        Version.parse("v2.0.0"), //
                        Version.parse("1.3.0"), //
                        Version.parse("v1.3.0"), //
                        Version.parse("1.2.4"), //
                        Version.parse("v1.2.4") //
                ));
    }
}
