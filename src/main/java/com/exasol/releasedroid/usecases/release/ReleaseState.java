package com.exasol.releasedroid.usecases.release;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.ReleaseException;
import com.exasol.releasedroid.usecases.request.PlatformName;

/**
 * This class is responsible for the release state.
 */
public class ReleaseState {
    private final String directory;

    /**
     * Create a new instance of {@link ReleaseState}.
     *
     * @param directory directory
     */
    public ReleaseState(final String directory) {
        this.directory = directory;
    }

    /**
     * Save release progress.
     *
     * @param repositoryName repository name
     * @param releaseVersion release version
     * @param platformName   platform name
     * @param releaseOutput  output to save
     */
    public void saveProgress(final String repositoryName, final String releaseVersion, final PlatformName platformName,
            final String releaseOutput) {
        final Path pathToDirectory = Path.of(this.directory);
        if (!Files.exists(pathToDirectory)) {
            createDirectory(pathToDirectory);
        }
        final Path pathToProgressFile = getPathToProgressFile(repositoryName, releaseVersion);
        if (!Files.exists(pathToProgressFile)) {
            createProgressFile(pathToProgressFile);
        }
        writeProgress(pathToProgressFile, platformName.toString(), releaseOutput);
    }

    private void createDirectory(final Path directory) {
        try {
            Files.createDirectory(directory);
        } catch (final IOException exception) {
            throw new ReleaseException(ExaError.messageBuilder("E-RD-13")
                    .message("Unable to create the release progress directory: {{directory}}", this.directory)
                    .toString(), exception);
        }
    }

    private void createProgressFile(final Path pathToProgressFile) {
        try {
            Files.createFile(pathToProgressFile);
        } catch (final IOException exception) {
            throw new ReleaseException(ExaError.messageBuilder("E-RD-11")
                    .message("Unable to create the release progress file.")
                    .mitigation(
                            "Please make sure that your use has writing access to the following directory: {{directory}}",
                            this.directory)
                    .toString(), exception);
        }
    }

    private void writeProgress(final Path pathToProgressFile, final String platformName, final String releaseOutput) {
        try {
            Files.write(pathToProgressFile, renderWriteString(platformName, releaseOutput).getBytes(),
                    StandardOpenOption.APPEND);
        } catch (final IOException exception) {
            throw new ReleaseException(ExaError.messageBuilder("E-RD-14")
                    .message("Unable to save the release progress to the file {{pathToProgressFile}}",
                            pathToProgressFile)
                    .mitigation("Please make sure that your use has writing access to the mentioned file.").toString(),
                    exception);
        }
    }

    private String renderWriteString(final String platformName, final String releaseOutput) {
        return platformName + "::" + releaseOutput + LINE_SEPARATOR;
    }

    /**
     * Get the release progress.
     *
     * @param repositoryName repository name
     * @param releaseVersion release version
     * @return map of platforms where release succeeded with platform output
     */
    public Map<PlatformName, String> getProgress(final String repositoryName, final String releaseVersion) {
        final Path pathToProgressFile = getPathToProgressFile(repositoryName, releaseVersion);
        if (Files.exists(pathToProgressFile)) {
            return extractProgress(pathToProgressFile);
        } else {
            return Collections.emptyMap();
        }
    }

    private Path getPathToProgressFile(final String repositoryName, final String releaseVersion) {
        final String fileName = repositoryName.replace("/", "_") + "_" + releaseVersion;
        return Path.of(this.directory, fileName);
    }

    private Map<PlatformName, String> extractProgress(final Path path) {
        try (final Stream<String> lines = Files.lines(path)) {
            final Map<PlatformName, String> map = new EnumMap<>(PlatformName.class);
            lines.forEach(platform -> {
                final String[] split = platform.split("::");
                map.put(getPlatformName(split), getOutput(split));
            });
            return map;
        } catch (final IOException exception) {
            return Map.of();
        }
    }

    private String getOutput(final String[] releaseStateSplitLine) {
        return releaseStateSplitLine.length > 1 ? releaseStateSplitLine[1] : "";
    }

    private PlatformName getPlatformName(final String[] releaseStateSplitLine) {
        return PlatformName.valueOf(releaseStateSplitLine[0].toUpperCase(Locale.ROOT));
    }
}