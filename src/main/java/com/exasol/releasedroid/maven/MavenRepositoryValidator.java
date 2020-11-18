package com.exasol.releasedroid.maven;

import java.util.logging.Logger;

import com.exasol.releasedroid.repository.maven.MavenRepository;
import com.exasol.releasedroid.repository.maven.MavenPom;
import com.exasol.releasedroid.usecases.*;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

/**
 * This class validates a maven repository.
 */
public class MavenRepositoryValidator implements RepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(MavenRepositoryValidator.class.getName());

    @Override
    public Report validate(final Repository repository) {
        LOGGER.fine("Validating pom file content.");
        final MavenPom mavenPom = ((MavenRepository) repository).getMavenPom();
        final Report report = ReportImpl.validationReport();
        report.merge(validateVersion(mavenPom));
        report.merge(validateArtifactId(mavenPom));
        return report;
    }

    private Report validateVersion(final MavenPom mavenPom) {
        final Report report = ReportImpl.validationReport();
        if (mavenPom.hasVersion()) {
            report.addResult(ValidationResult.successfulValidation("'version' in the pom file exists."));
        } else {
            report.addResult(
                    ValidationResult.failedValidation("E-RR-VAL-11", "Cannot detect a 'version' in the pom file."));
        }
        return report;
    }

    private Report validateArtifactId(final MavenPom mavenPom) {
        final Report report = ReportImpl.validationReport();
        if (mavenPom.hasArtifactId()) {
            report.addResult(ValidationResult.successfulValidation("'artifactId' in the pom file exists."));
        } else {
            report.addResult(
                    ValidationResult.failedValidation("E-RR-VAL-12", "Cannot detect an 'artifactId' in the pom file."));
        }
        return report;
    }
}