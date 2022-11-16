package com.exasol.releasedroid.output.guide;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.exasol.releasedroid.usecases.repository.ReleaseConfig;
import com.exasol.releasedroid.usecases.request.PlatformName;

class Publication {

    private static final UrlBuilder MAVEN_URL = new UrlBuilder() //
            .prefix("https://repo1.maven.org/maven2/com/");

    private final Optional<ReleaseConfig> config;

    Publication(final Optional<ReleaseConfig> config) {
        this.config = config;
    }

    String mavenUrls(final String repoName, final String version) {
        if (!isMaven()) {
            return "";
        }
        return mavenArtifacts(repoName) //
                .map(a -> MAVEN_URL.build(a, version)) //
                .map(this::formatUrl) //
                .collect(Collectors.joining("<br />\n"));
    }

    String icons() {
        return ":github:" + (isMaven() ? " and :maven:" : "");
    }

    private Stream<String> mavenArtifacts(final String repoName) {
        final List<String> artifacts = this.config.get().getMavenArtifacts();
        return artifacts.isEmpty() ? Stream.of(repoName) : artifacts.stream();
    }

    private boolean isMaven() {
        if (this.config.isEmpty()) {
            return false;
        }
        return this.config.get().getReleasePlatforms().contains(PlatformName.MAVEN);
    }

    private String formatUrl(final String url) {
        return MessageFormat.format("<a href=\"{0}\">{0}</a>", url);
    }
}
