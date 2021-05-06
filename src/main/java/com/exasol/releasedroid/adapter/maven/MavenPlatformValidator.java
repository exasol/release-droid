package com.exasol.releasedroid.adapter.maven;

import static com.exasol.releasedroid.adapter.RepositoryValidatorHelper.validateFileExists;
import static com.exasol.releasedroid.adapter.RepositoryValidatorHelper.validateRepositories;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;

/**
 * This class checks if the project is ready for a release on Maven Central.
 */
public class MavenPlatformValidator implements ReleasePlatformValidator {
    protected static final String MAVEN_WORKFLOW_PATH = ".github/workflows/release_droid_release_on_maven_central.yml";
    private final MavenRepository repository;

    /**
     * Create a new instance of {@link MavenPlatformValidator}.
     *
     * @param repository repository
     */
    public MavenPlatformValidator(final MavenRepository repository) {
        this.repository = repository;
    }

    @Override
    // [impl->dsn~validate-maven-release-workflow-exists~1]
    public Report validate() {
        final var report = Report.validationReport();
        report.merge(validateRepositories(this.repository.getRepositoryValidators()));
        report.merge(validateFileExists(this.repository, MAVEN_WORKFLOW_PATH, "Workflow for a Maven release."));
        return report;
    }
}