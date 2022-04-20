package com.exasol.releasedroid.main;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.exasol.mavenprojectversiongetter.MavenProjectVersionGetter;

public class RunnerIT {

    @Test
    void jarIsExecutable() throws IOException, InterruptedException {
        String currentVersion = MavenProjectVersionGetter.getCurrentProjectVersion();
        Path jar = Paths.get("target").resolve("release-droid-" + currentVersion + ".jar").toAbsolutePath();
        if (!Files.exists(jar)) {
            fail("Jar " + jar + " does not exist. Run 'mvn package' before starting the integration tests");
        }
        List<String> command = List.of("java", "-jar", jar.toString(), "--help");
        String output = startCommand(command);
        assertThat(output, startsWith("usage: Release Droid"));
    }

    private String startCommand(List<String> command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            fail("Process failed with exit code " + exitCode + ", std error '" + readString(process.getErrorStream())
                    + "', std output '" + readString(process.getInputStream()) + "'.");
        }
        assertThat("exit code", exitCode, equalTo(0));
        return readString(process.getInputStream());
    }

    private String readString(InputStream stream) throws IOException {
        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
