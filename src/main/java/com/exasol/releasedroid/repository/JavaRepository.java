package com.exasol.releasedroid.repository;

import java.io.*;
import java.util.*;

import com.exasol.releasedroid.github.GithubGateway;
import com.exasol.releasedroid.maven.JavaRepositoryValidator;
import com.exasol.releasedroid.usecases.validate.GitRepositoryValidator;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.github.GitHubPlatformValidator;
import com.exasol.releasedroid.maven.MavenPlatformValidator;
import com.exasol.releasedroid.usecases.PlatformName;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * Maven-based Java repository.
 */
public class JavaRepository extends BaseRepository {
    private static final String POM_PATH = "pom.xml";
    private static final String PATH_TO_TARGET_DIR = "./target/";
    private final Map<PlatformName, RepositoryValidator> releaseablePlatforms;
    private final List<RepositoryValidator> platformValidators;
    private MavenPom pom;

    public JavaRepository(final RepositoryGate repositoryGate, final GithubGateway githubGateway) {
        super(repositoryGate);
        this.releaseablePlatforms = Map.of( //
                PlatformName.GITHUB, new GitHubPlatformValidator(this, githubGateway),
                PlatformName.MAVEN, new MavenPlatformValidator(this));
        this.platformValidators = List.of(new GitRepositoryValidator(this), new JavaRepositoryValidator(this));
        
    }

    @Override
    // [impl->dsn~users-add-upload-definition-files-for-their-deliverables~1]
    public Map<String, String> getDeliverables() {
        final String assetName = getAssetName() + ".jar";
        final String assetPath = PATH_TO_TARGET_DIR + assetName;
        return Map.of(assetName, assetPath);
    }

    private String getAssetName() {
        final Optional<String> deliverableName = parseDeliverableName(getMavenPom().getPlugins());
        final String artifactId = getArtifactId();
        return deliverableName.orElse(artifactId + "-" + getVersion());
    }

    private String getArtifactId() {
        if (getMavenPom().hasArtifactId()) {
            return getMavenPom().getArtifactId();
        } else {
            throw new RepositoryException(
                    ExaError.messageBuilder("E-RR-REP-2").message("Cannot find the project's artifactId.").toString());
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
        if (tag.equals("version") || tag.equals("project.version")) {
            return getVersion();
        } else {
            final Map<String, String> properties = getMavenPom().getProperties();
            if (properties.containsKey(tag)) {
                return properties.get(tag);
            } else {
                throw new IllegalStateException(
                        ExaError.messageBuilder("E-RR-REP-3").message("Cannot detect deliverable's name.").toString());
            }
        }
    }

    /**
     * Get a parsed Maven pom file.
     *
     * @return instance of {@link MavenPom}
     */
    public MavenPom getMavenPom() {
        if (this.pom == null) {
            this.pom = parsePom();
        }
        return this.pom;
    }

    @Override
    public Language getRepositoryLanguage() {
        return Language.JAVA;
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
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-RR-REP-4")
                            .message("Some problem happened during creating a temporary pom file.").toString(),
                    exception);
        }
    }

    @Override
    public String getVersion() {
        if (getMavenPom().hasVersion()) {
            return getMavenPom().getVersion();
        } else {
            throw new RepositoryException(ExaError.messageBuilder("E-RR-REP-5")
                    .message("Cannot find the current version in the repository.").toString());
        }
    }

    @Override
    public Map<PlatformName, RepositoryValidator> getValidatorForPlatforms() {
        return this.releaseablePlatforms;
    }

    @Override
    public List<RepositoryValidator> getStructureValidators() {
        return this.platformValidators;
    }
}