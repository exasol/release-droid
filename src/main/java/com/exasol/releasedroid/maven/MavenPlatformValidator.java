package com.exasol.releasedroid.maven;

import java.util.Map;

import com.exasol.releasedroid.repository.*;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.validate.AbstractRepositoryValidator;

/**
 * This class checks if the project is ready for a release on Maven Central.
 */
public class MavenPlatformValidator extends AbstractRepositoryValidator {
    private static final String PROJECT_KEEPER_PLUGIN_NAME = "project-keeper-maven-plugin";
    private static final String PROJECT_KEEPER_PLUGIN_VERSION = "0.5.0";
    protected static final String MAVEN_WORKFLOW_PATH = ".github/workflows/maven_central_release.yml";
    private final JavaRepository repository;

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

    private Report validateMavenPom(final MavenPom mavenPom) {
        final Report report = Report.validationReport();
        final Map<String, MavenPlugin> plugins = mavenPom.getPlugins();
        report.merge(validateProjectKeeperPlugin(plugins));
        return report;
    }

    // [impl->dsn~validate-pom-contains-required-plugins-for-maven-release~1]
    private Report validateProjectKeeperPlugin(final Map<String, MavenPlugin> plugins) {
        final Report report = Report.validationReport();
        final MavenPluginValidator mavenPluginValidator = new MavenPluginValidator(plugins);
        report.merge(mavenPluginValidator.validatePluginExists(PROJECT_KEEPER_PLUGIN_NAME));
        report.merge(mavenPluginValidator.validatePluginVersionEqualOrGreater(PROJECT_KEEPER_PLUGIN_NAME,
                PROJECT_KEEPER_PLUGIN_VERSION));
        return report;
    }
}
