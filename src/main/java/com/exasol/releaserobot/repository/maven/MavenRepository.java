package com.exasol.releaserobot.repository.maven;

import java.io.*;
import java.util.*;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.kohsuke.github.GHRepository;

import com.exasol.releaserobot.repository.GitRepositoryException;
import com.exasol.releaserobot.usecases.Repository;

/**
 * This class represents a Maven-based Java project's content.
 */
public class MavenRepository extends Repository {
    private static final String POM_PATH = "pom.xml";
    private static final String PATH_TO_TARGET_DIR = "./target/";
    private final MavenPom pom;

    /**
     * Create a new instance of {@link MavenRepository}.
     * 
     * @param repository an instance of {@link GHRepository}
     * @param branch     name of a branch to get content from
     * @param fullName   fully qualified name of the repository
     * @param latestTag  latest release tag
     */
    public MavenRepository(final GHRepository repository, final String branch, final String fullName,
            final Optional<String> latestTag) {
        super(repository, branch, fullName, latestTag);
        this.pom = parsePom();
    }

    private MavenPom parsePom() {
        final String pomContent = getSingleFileContentAsString(POM_PATH);
        final File temporaryPomFile = createTemporaryPomFile(pomContent);
        return new MavenPomParser(temporaryPomFile).parse();
    }

    private File createTemporaryPomFile(final String pom) {
        try {
            final File tempPomFile = File.createTempFile("pomProjection", null);
            tempPomFile.deleteOnExit();
            try (final BufferedWriter out = new BufferedWriter(new FileWriter(tempPomFile))) {
                out.write(pom);
            }
            return tempPomFile;
        } catch (final IOException exception) {
            throw new IllegalStateException("F-POM-1: Some problem happened during creating a temporary pom file",
                    exception);
        }
    }

    @Override
    public String getVersion() {
        if (this.pom.hasVersion()) {
            return this.pom.getVersion();
        } else {
            throw new GitRepositoryException("E-REP-GH-4: Cannot find the current version in the repository.");
        }
    }

    @Override
    // [impl->dsn~users-add-upload-definition-files-for-their-deliverables~1]
    public Map<String, String> getDeliverables() {
        final String assetName = getAssetName() + ".jar";
        final String assetPath = PATH_TO_TARGET_DIR + assetName;
        return Map.of(assetName, assetPath);
    }

    private String getAssetName() {
        final Optional<String> deliverableName = parseDeliverableName(this.pom.getPlugins());
        final String artifactId = getArtifactId();
        return deliverableName.orElse(artifactId + "-" + getVersion());
    }

    private String getArtifactId() {
        if (this.pom.hasArtifactId()) {
            return this.pom.getArtifactId();
        } else {
            throw new GitRepositoryException("E-REP-GH-5: Cannot find the project's artifactId.");
        }
    }

    private Optional<String> parseDeliverableName(final List<MavenPlugin> plugins) {
        for (final MavenPlugin plugin : plugins) {
            if (plugin.getArtifactId().equals("maven-assembly-plugin")) {
                return parseMavenAssemblyPlugin(plugin);
            }
        }
        return Optional.empty();
    }

    private Optional<String> parseMavenAssemblyPlugin(final MavenPlugin plugin) {
        final Xpp3Dom configurations = plugin.getConfiguration();
        if (configurations == null) {
            return Optional.empty();
        } else {
            return getParseConfigurations(configurations);
        }
    }

    private Optional<String> getParseConfigurations(final Xpp3Dom configurations) {
        final Xpp3Dom finalName = configurations.getChild("finalName");
        if ((finalName == null) || (finalName.getValue() == null) || finalName.getValue().isEmpty()) {
            return Optional.empty();
        } else {
            return parseFinalName(finalName);
        }
    }

    private Optional<String> parseFinalName(final Xpp3Dom finalNameNode) {
        String finalName = finalNameNode.getValue().strip();
        while (finalName.contains("${")) {
            finalName = replaceVariable(finalName);
        }
        return Optional.of(finalName);
    }

    private String replaceVariable(final String finalName) {
        final int startIndex = finalName.indexOf("${") + 2;
        final int endIndex = finalName.indexOf('}');
        final String tag = finalName.substring(startIndex, endIndex);
        final String replacement = findReplacement(tag);
        return finalName.replace("${" + tag + "}", replacement);
    }

    private String findReplacement(final String tag) {
        if (tag.equals("version")) {
            return getVersion();
        } else {
            final Map<String, String> properties = this.pom.getProperties();
            if (properties.containsKey(tag)) {
                return properties.get(tag);
            } else {
                throw new IllegalStateException("F-POM-2: Cannot detect deliverable's name.");
            }
        }
    }

    /**
     * Get a parsed Maven pom file.
     *
     * @return instance of {@link MavenPom}
     */
    public MavenPom getMavenPom() {
        return this.pom;
    }
}