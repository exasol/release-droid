package com.exasol.releasedroid.usecases.release;

import static com.exasol.releasedroid.usecases.request.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ReleaseStateTest {
    @Test
    void saveProgressCreateFile() throws IOException {
        final ReleaseState state = getReleaseState();
        state.saveProgress("exasol/release-droid", "0.5.0", GITHUB);
        state.saveProgress("exasol/release-droid", "0.5.0", MAVEN);
        state.saveProgress("exasol/release-droid", "0.5.0", COMMUNITY);
        assertThat(state.getProgress("exasol/release-droid", "0.5.0"), equalTo(Set.of(GITHUB, MAVEN, COMMUNITY)));
    }

    @Test
    void testGetProgressMissingFile() throws IOException {
        final ReleaseState state = getReleaseState();
        assertThat(state.getProgress("exasol/release-droid", "0.5.0"), equalTo(Set.of()));
    }

    private ReleaseState getReleaseState() throws IOException {
        final Path tempDirectory = Files.createTempDirectory("temp-release-droid");
        return new ReleaseState(tempDirectory.toString());
    }

    @Test
    void testGetProgress() throws IOException {
        final Path tempDirectory = Files.createTempDirectory("temp-release-droid");
        final Path tempFile = Files.createFile(Path.of(tempDirectory.toString(), "exasol_release-droid_0.5.0"));
        try (final BufferedWriter out = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            out.write("GITHUB\nMaven\ncoMMunitY");
        }
        final ReleaseState state = new ReleaseState(tempDirectory.toString());
        assertThat(state.getProgress("exasol/release-droid", "0.5.0"), equalTo(Set.of(GITHUB, MAVEN, COMMUNITY)));
    }
}