package com.exasol.releasedroid.usecases.repository.version;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ModuleTest {
    @Test
    void equalsContract() {
        EqualsVerifier.simple().forClass(Module.class).verify();
    }

    @Test
    void isGolang() {
        assertThat(new Module("maven", "a/b/c").isGolang(), is(false));
        assertThat(new Module("golang", "a/b/c").isGolang(), is(true));
    }

    @Test
    void isSubfolder() {
        assertThat(new Module("maven", "pom.xml").isSubfolder(), is(false));
        assertThat(new Module("golang", "go.mod").isSubfolder(), is(false));
        assertThat(new Module("maven", "a/pom.xml").isSubfolder(), is(true));
        assertThat(new Module("golang", "b/go.mod").isSubfolder(), is(true));
    }
}
