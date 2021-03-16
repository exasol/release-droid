package com.exasol.releasedroid.repository;

import java.io.*;
import java.util.*;

import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.exasol.errorreporting.ExaError;

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
            throw new RepositoryException(ExaError.messageBuilder("E-POM-1").message("Cannot parse pom.xml file.")
                    .mitigation("Please, check if the 'pom.xml' file exists and has a valid format.").toString(),
                    exception);
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
        final Map<String, MavenPlugin> plugins = parsePlugins(properties);
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

    private Map<String, MavenPlugin> parsePlugins(final Map<String, String> properties) {
        final Build build = this.model.getBuild();
        if (build == null) {
            return Collections.emptyMap();
        }
        final Map<String, MavenPlugin> plugins = new HashMap<>();
        for (final Plugin plugin : build.getPlugins()) {
            final String artifactId = plugin.getArtifactId();
            final String version = getString(plugin, properties);
            final MavenPlugin mavenPlugin = MavenPlugin.builder().artifactId(artifactId).version(version).build();
            plugins.put(artifactId, mavenPlugin);
        }
        return plugins;
    }

    private String getString(final Plugin plugin, final Map<String, String> properties) {
        final String version = plugin.getVersion();
        if (version.contains("$")) {
            return findProperty(version.substring(2, version.length() - 1), properties);
        } else {
            return version;
        }
    }

    private String findProperty(final String property, final Map<String, String> properties) {
        return properties.getOrDefault(property, "");
    }
}