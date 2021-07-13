package com.exasol.releasedroid.adapter.maven;

import static com.exasol.releasedroid.adapter.RepositoryValidatorHelper.validateFileExists;
import static com.exasol.releasedroid.adapter.RepositoryValidatorHelper.validateRepositories;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
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
        final var report = ValidationReport.create();
        report.merge(validateRepositories(this.repository.getRepositoryValidators()));
        report.merge(validateFileExists(this.repository, MAVEN_WORKFLOW_PATH, "Workflow for a Maven release."));
        report.merge(validatePom(this.repository.getMavenPom()));
        return report;
    }

    private Report validatePom(final MavenPom mavenPom) {
        final var report = ValidationReport.create();
        report.merge(validateMavenPomPart(mavenPom.hasProjectDescription(), "Project description"));
        report.merge(validateMavenPomPart(mavenPom.hasProjectURL(), "Project URL"));
        return report;
    }

    private Report validateMavenPomPart(final boolean present, final String name) {
        final var report = ValidationReport.create();
        if (present) {
            report.addSuccessfulResult(name + " presents in pom.xml file.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-20")
                    .message("{{name|uq}} is missing in the pom.xml file.", name).toString());
        }
        return report;
    }
}