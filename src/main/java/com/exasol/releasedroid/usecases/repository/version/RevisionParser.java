package com.exasol.releasedroid.usecases.repository.version;

import java.util.*;

import org.yaml.snakeyaml.Yaml;

/**
 * Configuration of a project describing the modules contained in the project's repository.
 */
public class RevisionParser {

    private RevisionParser() {
        // only static usage
    }

    /**
     * @param changelog     contents of repository's changelog file
     * @param configuration contents of repository's project configuration file
     * @return {@link Revision} providing information about the repository's version and git tags
     * @throws ConfigurationException
     * @throws ChangelogException
     */
    public static Revision parse(final String changelog, final String configuration)
            throws ChangelogException, ConfigurationException {
        return Revision.from(getVersionFromChangelog(changelog), parseYaml(configuration));
    }

    private static String getVersionFromChangelog(final String changelog) throws ChangelogException {
        final int from = changelog.indexOf('[');
        final int to = changelog.indexOf(']');
        if ((from == -1) || (to == -1) || (to < from)) {
            throw new ChangelogException();
        }
        return changelog.substring(from + 1, to);
    }

    // [impl->dsn~creating-git-tags~1]
    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> parseYaml(final String contents) throws ConfigurationException {
        if (contents.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            final Map<String, Object> yaml = new Yaml().load(contents);
            final var result = yaml.get("sources");
            return (result == null) ? Collections.emptyList() : (List<Map<String, Object>>) result;
        } catch (final ClassCastException exception) {
            throw new ConfigurationException(exception);
        }
    }

    public static class ChangelogException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class ConfigurationException extends Exception {
        private static final long serialVersionUID = 1L;

        public ConfigurationException(final ClassCastException exception) {
            super(exception);
        }
    }
}
