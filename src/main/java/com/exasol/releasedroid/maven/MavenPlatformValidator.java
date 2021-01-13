package com.exasol.releasedroid.maven;

import java.util.*;

import org.apache.maven.model.PluginExecution;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.repository.*;
import com.exasol.releasedroid.usecases.report.*;
import com.exasol.releasedroid.usecases.validate.AbstractPlatformValidator;

/**
 * This class checks if the project is ready for a release on Maven Central.
 */
public class MavenPlatformValidator extends AbstractPlatformValidator {
    private static final List<String> REQUIRED_PLUGINS = List.of("nexus-staging-maven-plugin", "maven-source-plugin",
            "maven-gpg-plugin", "maven-javadoc-plugin", "maven-deploy-plugin");
    protected static final String MAVEN_WORKFLOW_PATH = ".github/workflows/maven_central_release.yml";

    final JavaRepository repository;

    public MavenPlatformValidator(final JavaRepository repository) {
        this.repository = repository;
    }

    @Override
    // [impl->dsn~validate-maven-release-workflow-exists~1]
    public Report validate() {
        final Report report = Report.validationReport();
        report.merge(validateFileExists(this.repository, MAVEN_WORKFLOW_PATH, "Workflow for a Maven release."));
        report.merge(validateMavenPom(this.repository.getMavenPom()));
        return report;
    }

    // [impl->dsn~validate-pom-contains-required-plugins-for-maven-release~1]
    private Report validateMavenPom(final MavenPom mavenPom) {
        final Report report = Report.validationReport();
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
        return Report.validationReport();
    }

    private Report validateGpgPluginExecutions(final MavenPlugin plugin) {
        final Report report = Report.validationReport();
        if (plugin.hasExecutions()) {
            report.addResult(validateExecutions(plugin.getExecutions()));
        } else {
            report.addResult(ValidationResult.failedValidation(
                    ExaError.messageBuilder("E-RR-VAL-14").message("The 'maven-gpg-plugin' misses executions.")
                            .mitigation("Please, check the user guide to add them.").toString()));
        }
        return report;
    }

    private Result validateExecutions(final List<PluginExecution> executions) {
        for (final PluginExecution execution : executions) {
            if (execution.getId().equals("sign-artifacts")) {
                return validateConfigurations(execution);
            }
        }
        return ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-15")
                .message("The 'maven-gpg-plugin' misses 'sign-artifacts' execution.")
                .mitigation("Please, check the user guide to add it.").toString());
    }

    private Result validateConfigurations(final PluginExecution execution) {
        final Object configuration = execution.getConfiguration();
        if ((configuration == null) || !configuration.toString().contains("--pinentry-mode")) {
            return ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-7")
                    .message("The 'maven-gpg-plugin' misses configuration of the 'sign-artifacts' execution.")
                    .mitigation("Please, check the user guide to add it.").toString());
        } else {
            return ValidationResult.successfulValidation("'maven-gpg-plugin' has required configurations.");
        }
    }

    private Report validatePluginsList(final List<MavenPlugin> plugins) {
        final Report report = Report.validationReport();
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
            return ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-13")
                    .message("Required maven plugin is missing: {{requiredPlugin}}.")
                    .parameter("requiredPlugin", requiredPlugin).toString());
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
