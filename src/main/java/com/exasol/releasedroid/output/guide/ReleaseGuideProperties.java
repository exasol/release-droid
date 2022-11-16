package com.exasol.releasedroid.output.guide;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import com.exasol.releasedroid.usecases.PropertyReader;

// [impl->dsn~configure-actual-urls~1]
class ReleaseGuideProperties implements PropertyReader {

    private static final String RELEASE_CHECKLISTS_KEY = "release_checklists";
    private static final String TEAMPLANNING_KEY = "team_planning";

    static ReleaseGuideProperties from(final Path path) {
        final Properties properties = new Properties();
        try (final InputStream stream = Files.newInputStream(path)) {
            properties.load(stream);
        } catch (final IOException exception) {
            // class XProperties is designed to not throw an exception in case read from file fails
        }
        return new ReleaseGuideProperties(path, properties);
    }

    static String error(final String format, final Object... args) {
        return String.format("<span class=\"error\">(" + format + ")</span>", args);
    }

    private final Path path;
    private final Properties properties;

    ReleaseGuideProperties(final Path path, final Properties properties) {
        this.path = path;
        this.properties = properties;
    }

    String releaseChecklists() {
        final String url = getProperty(RELEASE_CHECKLISTS_KEY);
        return url != null //
                ? "Create new checklist on page <a href=\"" + url + "\">Release checklists</a>"
                : valueOrError(RELEASE_CHECKLISTS_KEY);
    }

    String teamPlanning() {
        final String url = getProperty(TEAMPLANNING_KEY);
        return url != null //
                ? String.format("<a href=\"%s\">%s</a>", url, label(url))
                : valueOrError(TEAMPLANNING_KEY);
    }

    String announceChannel(final TargetAudience targetAudience) {
        if (!targetAudience.available()) {
            return error("unknown");
        }
        final AnnounceChannel channel = AnnounceChannel.find(targetAudience.display());
        if (channel.property == null) {
            return error("unsupported target audience '%s'", targetAudience);
        }
        final String url = this.properties.getProperty(channel.property);
        return url != null //
                ? String.format("<a href=\"%s\">%s</a>", url, channel.label)
                : valueOrError(channel.property);
    }

    private String getProperty(final String key) {
        return this.properties.getProperty(key);
    }

    private String label(final String url) {
        return url.substring(url.lastIndexOf("=") + 1);
    }

    private String valueOrError(final String key) {
        final String value = getProperty(key);
        return value != null //
                ? value
                : error("no entry '<code>%s</code>' in file <code>%s</code>", key, this.path);
    }

    @Override
    public String readProperty(final String key, final boolean hide) {
        return getProperty(key);
    }
}