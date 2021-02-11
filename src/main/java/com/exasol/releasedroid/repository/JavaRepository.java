package com.exasol.releasedroid.repository;

import java.io.*;
import java.util.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.github.GitHubPlatformValidator;
import com.exasol.releasedroid.github.GithubGateway;
import com.exasol.releasedroid.maven.JavaRepositoryValidator;
import com.exasol.releasedroid.maven.MavenPlatformValidator;
import com.exasol.releasedroid.usecases.PlatformName;
import com.exasol.releasedroid.usecases.validate.GitRepositoryValidator;
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
                PlatformName.GITHUB, new GitHubPlatformValidator(this, githubGateway), PlatformName.MAVEN,
                new MavenPlatformValidator(this));
        this.platformValidators = List.of(new GitRepositoryValidator(this), new JavaRepositoryValidator(this));
    }

    @Override
    // [impl->dsn~users-add-upload-definition-files-for-their-deliverables~1]
    public Map<String, String> getDeliverables() {
        final Map<String, String> deliverables = new HashMap<>();
        final List<String> jarsNames = collectJarsNames();
        for (final String jarsName : jarsNames) {
            deliverables.put(jarsName, PATH_TO_TARGET_DIR + jarsName);
        }
        return deliverables;
    }

    private List<String> collectJarsNames() {
        final List<String> jarsNames = new ArrayList<>(3);
        final String projectJarPattern = getProjectJarName();
        jarsNames.add(projectJarPattern + ".jar");
        if (hasSourceJar()) {
            jarsNames.add(projectJarPattern + "-sources.jar");
        }
        if (hasJavadocJar()) {
            jarsNames.add(projectJarPattern + "-javadoc.jar");
        }
        return jarsNames;
    }

    private boolean hasJavadocJar() {
        return hasPlugin("maven-javadoc-plugin");
    }

    private boolean hasSourceJar() {
        return hasPlugin("maven-source-plugin");
    }

    private boolean hasPlugin(final String pluginName) {
        final List<MavenPlugin> plugins = getMavenPom().getPlugins();
        for (final MavenPlugin plugin : plugins) {
            if (plugin.getArtifactId().equals(pluginName)) {
                return true;
            }
        }
        return false;
    }

    private String getProjectJarName() {
        final Optional<String> deliverableName = parseDeliverableName();
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

    private Optional<String> parseDeliverableName() {
        final Map<String, String> properties = getMavenPom().getProperties();
        if (properties.containsKey("final.name")) {
            final String finalName = parseFinalName(properties.get("final.name"));
            return Optional.of(finalName);
        } else {
            return Optional.empty();
        }
    }

    private String parseFinalName(String finalName) {
        while (finalName.contains("${")) {
            finalName = replaceVariable(finalName);
        }
        return finalName;
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
            return getRequiredPropertyValue(tag);
        }
    }

    private String getRequiredPropertyValue(final String key) {
        final Map<String, String> properties = getMavenPom().getProperties();
        if (properties.containsKey(key)) {
            return properties.get(key);
        } else {
            throw new IllegalStateException(
                    ExaError.messageBuilder("E-RR-REP-3").message("Cannot detect property {{propertyKey}}.") //
                            .parameter("propertyKey", key) //
                            .mitigation("Please check your pom.xml file.").toString());
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