package com.exasol.releasedroid.maven;

import java.util.Map;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.repository.MavenPlugin;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;

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
        final Report report = Report.validationReport();
        if (this.plugins.containsKey(pluginName)) {
            report.addResult(ValidationResult.successfulValidation("Maven plugin '" + pluginName + "'."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-13")
                    .message("Required maven plugin is missing: {{requiredPlugin}}.")
                    .parameter("requiredPlugin", pluginName).toString()));
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
        final Report report = Report.validationReport();
        if (this.plugins.containsKey(pluginName)) {
            report.merge(validatePluginVersion(pluginName, expectedVersion));
        }
        return report;
    }

    private Report validatePluginVersion(final String pluginName, final String expectedVersion) {
        final Report report = Report.validationReport();
        final String actualVersion = this.plugins.get(pluginName).getVersion();
        if (compare(expectedVersion, actualVersion)) {
            report.addResult(
                    ValidationResult.successfulValidation("Maven plugin '" + pluginName + "' version is correct."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-14").message(
                    "Maven plugin {{pluginName}} has invalid version. The version should be {{version}} or higher.")
                    .parameter("pluginName", pluginName) //
                    .parameter("version", expectedVersion) //
                    .toString()));
        }
        return report;
    }

    private boolean compare(final String expectedVersion, final String actualVersion) {
        if (expectedVersion.equals(actualVersion)) {
            return true;
        } else {
            final String[] expected = expectedVersion.split("\\.");
            final String[] actual = actualVersion.split("\\.");
            return compare(expected, actual);
        }
    }

    private boolean compare(final String[] expected, final String[] actual) {
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