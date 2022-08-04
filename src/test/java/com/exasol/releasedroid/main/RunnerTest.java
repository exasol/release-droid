package com.exasol.releasedroid.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class RunnerTest {

    @Test
    void createReleaseDroid() throws IOException {
        assertThat(Runner.createReleaseDroid(), notNullValue());
    }

    @Test
    void checkCredentialsFile() throws IOException {
        assertThat(Runner.checkCredentialsFile(Paths.get("/non/existing/file")), is(false));
    }
}
