package com.exasol.releasedroid.maven;

import java.util.Map;
import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.repository.*;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * This class validates a maven repository.
 */
public class JavaRepositoryValidator implements RepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(JavaRepositoryValidator.class.getName());
    private static final String REPRODUCIBLE_BUILD_MAVEN_PLUGIN = "reproducible-build-maven-plugin";
    private static final String PROJECT_KEEPER_PLUGIN_NAME = "project-keeper-maven-plugin";
    private static final String PROJECT_KEEPER_PLUGIN_VERSION = "0.5.0";
    private final JavaRepository repository;

    public JavaRepositoryValidator(final JavaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Report validate() {
        LOGGER.fine("Validating pom file content.");
        final MavenPom mavenPom = this.repository.getMavenPom();
        final Report report = Report.validationReport();
        report.merge(validateVersion(mavenPom));
        report.merge(validateArtifactId(mavenPom));
        report.merge(validateReproducibleBuildPlugin(mavenPom));
        report.merge(validateProjectKeeperPlugin(mavenPom));
        return report;
    }

    private Report validateVersion(final MavenPom mavenPom) {
        final Report report = Report.validationReport();
        if (mavenPom.hasVersion()) {
            report.addResult(ValidationResult.successfulValidation("'version' in the pom file exists."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-11")
                    .message("Cannot detect a 'version' in the pom file.").toString()));
        }
        return report;
    }

    private Report validateArtifactId(final MavenPom mavenPom) {
        final Report report = Report.validationReport();
        if (mavenPom.hasArtifactId()) {
            report.addResult(ValidationResult.successfulValidation("'artifactId' in the pom file exists."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-12")
                    .message("Cannot detect an 'artifactId' in the pom file.").toString()));
        }
        return report;
    }

    private Report validateReproducibleBuildPlugin(final MavenPom mavenPom) {
        final Report report = Report.validationReport();
        final Map<String, MavenPlugin> plugins = mavenPom.getPlugins();
        final MavenPluginValidator mavenPluginValidator = new MavenPluginValidator(plugins);
        report.merge(mavenPluginValidator.validatePluginExists(REPRODUCIBLE_BUILD_MAVEN_PLUGIN));
        return report;
    }

    // [impl->dsn~validate-pom-contains-required-plugins-for-maven-release~1]
    private Report validateProjectKeeperPlugin(final MavenPom mavenPom) {
        final Report report = Report.validationReport();
        final MavenPluginValidator mavenPluginValidator = new MavenPluginValidator(mavenPom.getPlugins());
        report.merge(mavenPluginValidator.validatePluginExists(PROJECT_KEEPER_PLUGIN_NAME));
        if (mavenPom.getArtifactId().equals(PROJECT_KEEPER_PLUGIN_NAME)) {
            report.addResult(ValidationResult.successfulValidation("Skipping version check for the "
                    + PROJECT_KEEPER_PLUGIN_NAME + " in the plugin repository itself."));
        } else {
            report.merge(mavenPluginValidator.validatePluginVersionEqualOrGreater(PROJECT_KEEPER_PLUGIN_NAME,
                    PROJECT_KEEPER_PLUGIN_VERSION));
        }
        return report;
    }
}