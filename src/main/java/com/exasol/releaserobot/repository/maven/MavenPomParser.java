package com.exasol.releaserobot.repository.maven;

import java.io.*;
import java.util.*;

import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.exasol.releaserobot.repository.GitRepositoryException;

/**
 * Parser for {@link MavenPom}.
 */
public class MavenPomParser {
    private final Model model;

    /**
     * Create a new instance of {@link MavenPomParser}.
     * 
     * @param pom pom file
     */
    public MavenPomParser(final File pom) {
        this.model = createModel(pom);
    }

    private Model createModel(final File tempPomFile) {
        try (final Reader reader = new FileReader(tempPomFile)) {
            final MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
            return xpp3Reader.read(reader);
        } catch (final XmlPullParserException | IOException exception) {
            throw new GitRepositoryException("E-POM-1: Cannot parse pom.xml file. "
                    + "Please, check if the 'pom.xml' file exists and has a valid format.", exception);
        }
    }

    /**
     * Parse pom file.
     * 
     * @return new instance of {@link MavenPom}
     */
    public MavenPom parse() {
        final String artifactId = this.model.getArtifactId();
        final String version = this.model.getVersion();
        final Map<String, String> properties = parseProperties();
        final List<MavenPlugin> plugins = parsePlugins();
        return MavenPom.builder().version(version).artifactId(artifactId).properties(properties).plugins(plugins)
                .build();
    }

    private Map<String, String> parseProperties() {
        final Map<String, String> properties = new HashMap<>();
        final Properties modelProperties = this.model.getProperties();
        for (final String propertyName : modelProperties.stringPropertyNames()) {
            properties.put(propertyName, modelProperties.getProperty(propertyName));
        }
        return properties;
    }

    private List<MavenPlugin> parsePlugins() {
        final Build build = this.model.getBuild();
        if (build == null) {
            return Collections.emptyList();
        }
        final List<MavenPlugin> plugins = new ArrayList<>();
        for (final Plugin plugin : build.getPlugins()) {
            final String artifactId = plugin.getArtifactId();
            final Xpp3Dom configurations = (Xpp3Dom) plugin.getConfiguration();
            final MavenPlugin mavenPlugin = MavenPlugin.builder().artifactId(artifactId).configuration(configurations)
                    .build();
            plugins.add(mavenPlugin);
        }
        return plugins;
    }
}