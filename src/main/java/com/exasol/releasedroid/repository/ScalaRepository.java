package com.exasol.releasedroid.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.util.Map;

import com.exasol.errorreporting.ExaError;

/**
 * Sbt-based scala repository.
 */
public class ScalaRepository extends BaseRepository {
    private static final String PATH_TO_TARGET_DIR = "./target/scala-2.12/";

    public ScalaRepository(final RepositoryGate repositoryGate) {
        super(repositoryGate);
    }

    @Override
    public String getVersion() {
        final String changelog = getChangelogFile();
        final String[] lines = changelog.split(LINE_SEPARATOR);
        if (lines.length > 1) {
            for (final String line : lines) {
                final int startIndex = line.indexOf("[");
                final int endIndex = line.indexOf("]");
                if (startIndex >= 0 && endIndex > startIndex) {
                    return line.substring(startIndex + 1, endIndex);
                }
            }
        }
        throw new RepositoryException(ExaError.messageBuilder("E-RR-REP-9")
                .message("Cannot detect Scala's project version in 'changelog.md' file.")
                .mitigation("Please make sure that you added filled a 'changelog.md' file according to a user guide.")
                .toString());
    }

    @Override
    public Language getRepositoryLanguage() {
        return Language.SCALA;
    }

    @Override
    public Map<String, String> getDeliverables() {
        final String buildFile = getSingleFileContentAsString("build.sbt");
        final String projectName = getProjectName(buildFile);
        final String assetName = projectName + "-" + getVersion() + ".jar";
        final String assetPath = PATH_TO_TARGET_DIR + assetName;
        return Map.of(assetName, assetPath);
    }

    private String getProjectName(final String buildFile) {
        if (buildFile.contains("moduleName")) {
            final int moduleName = buildFile.indexOf("moduleName");
            final int start = buildFile.indexOf("\"", moduleName);
            final int end = buildFile.indexOf("\"", start + 1);
            return buildFile.substring(start + 1, end);
        } else {
            return getName().split("/")[1];
        }
    }
}