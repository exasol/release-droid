package com.exasol.releasedroid.adapter.maven;

import java.util.Map;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;

/**
 * Validator for Maven plugins.
 */
public class MavenPluginValidator {
    private final Map<String, MavenPlugin> plugins;

    /**
     * Create a new instance of {@link MavenPluginValidator}.
     *
     * @param plugins map with project's maven plugins.
     */
    public MavenPluginValidator(final Map<String, MavenPlugin> plugins) {
        this.plugins = plugins;
    }

    /**
     * Validate that plugins exists.
     *
     * @param pluginName plugin name to validate
     * @return instance of {@link Report}
     */
    public Report validatePluginExists(final String pluginName) {
        final var report = ValidationReport.create();
        if (this.plugins.containsKey(pluginName)) {
            report.addSuccessfulResult("Maven plugin '" + pluginName + "'.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-15")
                    .message("Required maven plugin is missing: {{requiredPlugin}}.", pluginName).toString());
        }
        return report;
    }

    /**
     * Validate the the version of a plugin is equal or greater than expected.
     *
     * @param pluginName      plugin name to validate
     * @param expectedVersion expected version to compare against
     * @return instance of {@link Report}
     */
    public Report validatePluginVersionEqualOrGreater(final String pluginName, final String expectedVersion) {
        final var report = ValidationReport.create();
        if (this.plugins.containsKey(pluginName)) {
            report.merge(validatePluginVersion(pluginName, expectedVersion));
        }
        return report;
    }

    private Report validatePluginVersion(final String pluginName, final String expectedVersion) {
        final var report = ValidationReport.create();
        final MavenPlugin plugin = this.plugins.get(pluginName);
        if (plugin.hasVersion() && compareSemanticVersion(expectedVersion, plugin.getVersion())) {
            report.addSuccessfulResult("Maven plugin '" + pluginName + "' version is correct.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-16")
                    .message("Maven plugin {{pluginName}} has invalid version or the version is not specified."
                            + " The version must be {{version}} or higher.")
                    .parameter("pluginName", pluginName) //
                    .parameter("version", expectedVersion) //
                    .toString());
        }
        return report;
    }

    private boolean compareSemanticVersion(final String expectedVersion, final String actualVersion) {
        if (expectedVersion.equals(actualVersion)) {
            return true;
        } else {
            final String[] expected = expectedVersion.split("\\.");
            final String[] actual = actualVersion.split("\\.");
            return compareSemanticVersion(expected, actual);
        }
    }

    private boolean compareSemanticVersion(final String[] expected, final String[] actual) {
        if (actual.length != expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            final int expectedVersion = Integer.parseInt(expected[i]);
            final int actualVersion = Integer.parseInt(actual[i]);
            if (actualVersion > expectedVersion) {
                return true;
            } else if (actualVersion < expectedVersion) {
                return false;
            }
        }
        return true;
    }
}