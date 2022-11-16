package com.exasol.releasedroid.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter;

class RunnerIT {

    @Test
    void testJarIsExecutable() throws IOException, InterruptedException {
        final String currentVersion = MavenProjectVersionGetter.getCurrentProjectVersion();
        final Path jar = Path.of("target").resolve("release-droid-" + currentVersion + ".jar").toAbsolutePath();
        if (!Files.exists(jar)) {
            fail("Jar " + jar + " does not exist. Run 'mvn package' before starting the integration tests");
        }
        final String output = startCommand("java", "-jar", jar.toString(), "--help");
        assertThat(output, startsWith("usage: Release Droid"));
    }

    private String startCommand(final String... command) throws IOException, InterruptedException {
        final Process process = new ProcessBuilder(command).start();
        final int exitCode = process.waitFor();
        if (exitCode != 0) {
            fail("Process failed with exit code " + exitCode + ", std error '" + readString(process.getErrorStream())
                    + "', std output '" + readString(process.getInputStream()) + "'.");
        }
        assertThat("exit code", exitCode, equalTo(0));
        return readString(process.getInputStream());
    }

    private String readString(final InputStream stream) throws IOException {
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
