package com.exasol.releaserobot.repository.maven;

import java.io.*;
import java.util.*;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.kohsuke.github.GHRepository;

import com.exasol.releaserobot.repository.AbstractGitHubGitBranchContent;
import com.exasol.releaserobot.repository.GitHubGitRepository;

/**
 * This class represents a Maven-based Java project's content.
 */
public class JavaMavenGitBranchContent extends AbstractGitHubGitBranchContent {
    private static final String POM_PATH = "pom.xml";
    private static final String PATH_TO_TARGET_DIR = "./target/";
    private final MavenPom pom;

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     *
     * @param repository an instance of {@link GHRepository}
     * @param branch     name of a branch to get content from
     */
    public JavaMavenGitBranchContent(final GHRepository repository, final String branch) {
        super(repository, branch);
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
        return this.pom.getVersion();
    }

    @Override
    public Map<String, String> getDeliverables() {
        final String assetName = getAssetName() + ".jar";
        final String assetPath = PATH_TO_TARGET_DIR + assetName;
        return Map.of(assetName, assetPath);
    }

    private String getAssetName() {
        final Optional<String> deliverableName = parseDeliverableName(this.pom.getPlugins());
        return deliverableName.orElse(this.pom.getArtifactId() + "-" + this.pom.getVersion());
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
        if (finalName == null || finalName.getValue() == null || finalName.getValue().isEmpty()) {
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
            return this.pom.getVersion();
        } else {
            final Map<String, String> properties = this.pom.getProperties();
            if (properties.containsKey(tag)) {
                return properties.get(tag);
            } else {
                throw new IllegalStateException("F-POM-1: Cannot detect deliverable's name.");
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