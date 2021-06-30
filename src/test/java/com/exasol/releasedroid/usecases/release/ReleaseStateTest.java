package com.exasol.releasedroid.usecases.release;

import static com.exasol.releasedroid.usecases.request.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ReleaseStateTest {
    @Test
    void saveProgressCreateFile() throws IOException {
        final ReleaseState state = getReleaseState();
        final String linkToGitHub = "https://github.com/exasol/release-droid/releases/tag/0.5.0";
        final String linkToMaven = "";
        final String linkToCommunity = "https://community.exasol.com/some-link/150?prePageCrumb=BlogDashboardPage";
        state.saveProgress("exasol/release-droid", "0.5.0", GITHUB, linkToGitHub);
        state.saveProgress("exasol/release-droid", "0.5.0", MAVEN, linkToMaven);
        state.saveProgress("exasol/release-droid", "0.5.0", COMMUNITY, linkToCommunity);
        assertThat(state.getProgress("exasol/release-droid", "0.5.0"), equalTo(Map.of( //
                GITHUB, linkToGitHub, //
                MAVEN, linkToMaven, //
                COMMUNITY, linkToCommunity)));
    }

    @Test
    void testGetProgressMissingFile() throws IOException {
        final ReleaseState state = getReleaseState();
        assertThat(state.getProgress("exasol/release-droid", "0.5.0"), equalTo(Map.of()));
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
            out.write("GITHUB::\nMaven::\ncoMMunitY::some text here");
        }
        final ReleaseState state = new ReleaseState(tempDirectory.toString());
        assertThat(state.getProgress("exasol/release-droid", "0.5.0"), equalTo(Map.of( //
                GITHUB, "", MAVEN, "", COMMUNITY, "some text here")));
    }
}