package com.exasol.releasedroid.adapter.maven;

import com.exasol.releasedroid.adapter.AbstractRepositoryValidator;
import com.exasol.releasedroid.usecases.report.Report;

/**
 * This class checks if the project is ready for a release on Maven Central.
 */
public class MavenPlatformValidator extends AbstractRepositoryValidator {
    protected static final String MAVEN_WORKFLOW_PATH = ".github/workflows/release_droid_release_on_maven_central.yml";
    private final MavenRepository repository;

    public MavenPlatformValidator(final MavenRepository repository) {
        this.repository = repository;
    }

    @Override
    // [impl->dsn~validate-maven-release-workflow-exists~1]
    public Report validate() {
        final Report report = Report.validationReport();
        report.merge(validateFileExists(this.repository, MAVEN_WORKFLOW_PATH, "Workflow for a Maven release."));
        return report;
    }
}