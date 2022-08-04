package com.exasol.releasedroid.adapter.repository;

import java.util.logging.Logger;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.adapter.maven.MavenPom;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * This class validates a maven repository.
 */
public class JavaRepositoryValidator implements RepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(JavaRepositoryValidator.class.getName());
    private final JavaRepository repository;

    /**
     * Create a new instance of {@link JavaRepositoryValidator}.
     *
     * @param repository java repository
     */
    public JavaRepositoryValidator(final JavaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Report validate() {
        LOGGER.fine("Validating pom file content.");
        final MavenPom mavenPom = this.repository.getMavenPom();
        final var report = ValidationReport.create();
        report.merge(validateGroupId(mavenPom));
        report.merge(validateArtifactId(mavenPom));
        return report;
    }

    private Report validateGroupId(final MavenPom mavenPom) {
        final var report = ValidationReport.create();
        if (mavenPom.hasGroupId()) {
            report.addSuccessfulResult("'groupId' in the pom file exists.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-31")
                    .message("Cannot detect a 'groupId' in the pom file.").toString());
        }
        return report;
    }

    private Report validateArtifactId(final MavenPom mavenPom) {
        final var report = ValidationReport.create();
        if (mavenPom.hasArtifactId()) {
            report.addSuccessfulResult("'artifactId' in the pom file exists.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-13")
                    .message("Cannot detect an 'artifactId' in the pom file.").toString());
        }
        return report;
    }
}