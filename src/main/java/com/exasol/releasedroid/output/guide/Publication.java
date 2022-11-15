package com.exasol.releasedroid.output.guide;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.releasedroid.usecases.ReleaseDroidConstants;
import com.exasol.releasedroid.usecases.repository.*;

class Publication {

    static Publication create(final boolean isMaven, final RepositoryGate gate) {
        final String configuration = gate.getSingleFileContentAsString(ReleaseDroidConstants.RELEASE_CONFIG_PATH);
        final ReleaseConfig parsed = ReleaseConfigParser.parse(configuration);
        return new Publication(isMaven, parsed.getMavenArtifacts());
    }

    private static final UrlBuilder MAVEN_URL = new UrlBuilder() //
            .prefix("https://repo1.maven.org/maven2/com/");

    private final boolean publishedToMaven;
    private final List<String> mavenArtifacts;

    Publication(final boolean publishedToMaven, final List<String> mavenArtifacts) {
        this.publishedToMaven = publishedToMaven;
        this.mavenArtifacts = mavenArtifacts;
    }

    String mavenUrls(final String repoName, final String version) {
        return artifactNames(repoName) //
                .map(a -> MAVEN_URL.build(a, version)) //
                .map(this::formatUrl) //
                .collect(Collectors.joining("<br />\n"));
    }

    String icons() {
        return ":github:" + (this.publishedToMaven ? " and :maven:" : "");
    }

    private Stream<String> artifactNames(final String repoName) {
        if (!this.publishedToMaven) {
            return Stream.empty();
        }
        if (this.mavenArtifacts.isEmpty()) {
            return Stream.of(repoName);
        }
        return this.mavenArtifacts.stream();
    }

    private String formatUrl(final String url) {
        return MessageFormat.format("<a href=\"{0}\">{0}</a>", url);
    }
}
