package com.exasol.releasedroid.usecases.validate;

import java.util.Map;

import com.exasol.releasedroid.maven.MavenPluginValidator;
import com.exasol.releasedroid.repository.JavaRepository;
import com.exasol.releasedroid.repository.MavenPlugin;
import com.exasol.releasedroid.usecases.report.Report;

/**
 * A common validator for all Java repositories. This is a temporary validator that should be removed after the
 * validation is moved to the project-keeper-maven-plugin.
 */
public class JavaGitRepositoryValidator extends GitRepositoryValidator {
    private static final String REPRODUCIBLE_BUILD_MAVEN_PLUGIN = "reproducible-build-maven-plugin";
    private final JavaRepository javaRepository;

    public JavaGitRepositoryValidator(final JavaRepository repository) {
        super(repository);
        this.javaRepository = repository;
    }

    @Override
    public Report validate() {
        final Report report = Report.validationReport();
        report.merge(super.validate());
        report.merge(validateReproducibleBuildPlugin());
        return report;
    }

    protected Report validateReproducibleBuildPlugin() {
        final Report report = Report.validationReport();
        final Map<String, MavenPlugin> plugins = this.javaRepository.getMavenPom().getPlugins();
        final MavenPluginValidator mavenPluginValidator = new MavenPluginValidator(plugins);
        mavenPluginValidator.validatePluginExists(REPRODUCIBLE_BUILD_MAVEN_PLUGIN);
        return report;
    }
}