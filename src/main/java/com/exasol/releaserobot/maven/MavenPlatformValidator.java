package com.exasol.releaserobot.maven;

import java.util.*;

import org.apache.maven.model.PluginExecution;

import com.exasol.releaserobot.repository.maven.*;
import com.exasol.releaserobot.usecases.*;
import com.exasol.releaserobot.usecases.validate.AbstractPlatformValidator;

/**
 * This class checks if the project is ready for a release on Maven Central.
 */
public class MavenPlatformValidator extends AbstractPlatformValidator {
    private static final List<String> REQUIRED_PLUGINS = List.of("nexus-staging-maven-plugin", "maven-source-plugin",
            "maven-gpg-plugin", "maven-javadoc-plugin", "maven-deploy-plugin");
    protected static final String MAVEN_WORKFLOW_PATH = ".github/workflows/maven_central_release.yml";

    @Override
    public Report validate(final Repository repository) {
        final Report report = ReportImpl.validationReport();
        report.merge(validateFileExists(repository, MAVEN_WORKFLOW_PATH, "Workflow for a Maven release."));
        report.merge(validateMavenPom(((MavenRepository) repository).getMavenPom()));
        return report;
    }

    private Report validateMavenPom(final MavenPom mavenPom) {
        final Report report = ReportImpl.validationReport();
        final List<MavenPlugin> plugins = mavenPom.getPlugins();
        report.merge(validatePluginsList(plugins));
        report.merge(validateGpgPlugin(plugins));
        return report;
    }

    private Report validateGpgPlugin(final List<MavenPlugin> plugins) {
        for (final MavenPlugin plugin : plugins) {
            if (plugin.getArtifactId().equals("maven-gpg-plugin")) {
                return validateGpgPluginExecutions(plugin);
            }
        }
        return ReportImpl.validationReport();
    }

    private Report validateGpgPluginExecutions(final MavenPlugin plugin) {
        final Report report = ReportImpl.validationReport();
        if (plugin.hasExecutions()) {
            report.addResult(validateExecutions(plugin.getExecutions()));
        } else {
            report.addResult(ValidationResult.failedValidation("E-RR-VAL-14",
                    "The 'maven-gpg-plugin' misses executions. PLease, check the user guide to add them."));
        }
        return report;
    }

    private Result validateExecutions(final List<PluginExecution> executions) {
        for (final PluginExecution execution : executions) {
            if (execution.getId().equals("sign-artifacts")) {
                return validateConfigurations(execution);
            }
        }
        return ValidationResult.failedValidation("E-RR-VAL-15",
                "The 'maven-gpg-plugin' misses 'sign-artifacts' execution. PLease, check a user guide to add it.");
    }

    private Result validateConfigurations(final PluginExecution execution) {
        final Object configuration = execution.getConfiguration();
        if (configuration == null || !configuration.toString().contains("--pinentry-mode")) {
            return ValidationResult.failedValidation("E-RR-VAL-16",
                    "The 'maven-gpg-plugin' misses configuration of the 'sign-artifacts' execution. "
                            + "PLease, check the user guide to add it.");
        } else {
            return ValidationResult.successfulValidation("'maven-gpg-plugin' has required configurations.");
        }
    }

    private Report validatePluginsList(final List<MavenPlugin> plugins) {
        final Report report = ReportImpl.validationReport();
        final Set<String> pluginNames = getPluginNames(plugins);
        for (final String requiredPlugin : REQUIRED_PLUGINS) {
            report.addResult(validatePlugin(pluginNames, requiredPlugin));
        }
        return report;
    }

    private Result validatePlugin(final Set<String> pluginNames, final String requiredPlugin) {
        if (pluginNames.contains(requiredPlugin)) {
            return ValidationResult.successfulValidation("Maven plugin '" + requiredPlugin + "'.");
        } else {
            return ValidationResult.failedValidation("E-RR-VAL-13",
                    "Required maven plugin is missing: '" + requiredPlugin + "'.");
        }
    }

    private Set<String> getPluginNames(final List<MavenPlugin> plugins) {
        final Set<String> pluginNames = new HashSet<>();
        for (final MavenPlugin plugin : plugins) {
            pluginNames.add(plugin.getArtifactId());
        }
        return pluginNames;
    }
}