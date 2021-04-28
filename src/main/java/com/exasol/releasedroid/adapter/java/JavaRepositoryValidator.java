package com.exasol.releasedroid.adapter.java;

import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.maven.MavenPluginValidator;
import com.exasol.releasedroid.adapter.maven.MavenPom;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * This class validates a maven repository.
 */
public class JavaRepositoryValidator implements RepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(JavaRepositoryValidator.class.getName());
    private static final String PROJECT_KEEPER_PLUGIN_NAME = "project-keeper-maven-plugin";
    private static final String PROJECT_KEEPER_PLUGIN_VERSION = "0.6.0";
    private final JavaRepository repository;

    public JavaRepositoryValidator(final JavaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Report validate() {
        LOGGER.fine("Validating pom file content.");
        final MavenPom mavenPom = this.repository.getMavenPom();
        final var report = Report.validationReport();
        report.merge(validateVersion(mavenPom));
        report.merge(validateArtifactId(mavenPom));
        report.merge(validateProjectKeeperPlugin(mavenPom));
        return report;
    }

    private Report validateVersion(final MavenPom mavenPom) {
        final var report = Report.validationReport();
        if (mavenPom.hasVersion()) {
            report.addResult(ValidationResult.successfulValidation("'version' in the pom file exists."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-REP-12")
                    .message("Cannot detect a 'version' in the pom file.").toString()));
        }
        return report;
    }

    private Report validateArtifactId(final MavenPom mavenPom) {
        final var report = Report.validationReport();
        if (mavenPom.hasArtifactId()) {
            report.addResult(ValidationResult.successfulValidation("'artifactId' in the pom file exists."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RD-REP-13")
                    .message("Cannot detect an 'artifactId' in the pom file.").toString()));
        }
        return report;
    }

    // [impl->dsn~validate-pom-contains-required-plugins-for-maven-release~1]
    private Report validateProjectKeeperPlugin(final MavenPom mavenPom) {
        final var report = Report.validationReport();
        final var mavenPluginValidator = new MavenPluginValidator(mavenPom.getPlugins());
        report.merge(mavenPluginValidator.validatePluginExists(PROJECT_KEEPER_PLUGIN_NAME));
        report.merge(validateProjectKeeperVersion(mavenPom, mavenPluginValidator));
        return report;
    }

    private Report validateProjectKeeperVersion(final MavenPom mavenPom,
            final MavenPluginValidator mavenPluginValidator) {
        final var report = Report.validationReport();
        if (mavenPom.hasArtifactId() && !mavenPom.getArtifactId().equals(PROJECT_KEEPER_PLUGIN_NAME)) {
            report.merge(mavenPluginValidator.validatePluginVersionEqualOrGreater(PROJECT_KEEPER_PLUGIN_NAME,
                    PROJECT_KEEPER_PLUGIN_VERSION));
        }
        return report;
    }
}