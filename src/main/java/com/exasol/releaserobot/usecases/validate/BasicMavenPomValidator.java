package com.exasol.releaserobot.usecases.validate;

import java.util.logging.Logger;

import com.exasol.releaserobot.repository.GitRepository;
import com.exasol.releaserobot.repository.maven.JavaMavenGitBranchContent;
import com.exasol.releaserobot.repository.maven.MavenPom;
import com.exasol.releaserobot.usecases.*;

/**
 * This class validates a pom file.
 */
public class BasicMavenPomValidator implements RepositoryValidator {
    private static final Logger LOGGER = Logger.getLogger(BasicMavenPomValidator.class.getName());
    private final GitRepository repository;

    /**
     * Create a new instance of {@link BasicMavenPomValidator}.
     *
     * @param repository instance of {@link GitRepository} to validate
     *
     */
    public BasicMavenPomValidator(final GitRepository repository) {
        this.repository = repository;
    }

    @Override
    public Report validateDefaultBranch() {
        return validateBranch(this.repository.getDefaultBranchName());
    }

    @Override
    public Report validateBranch(final String branchName) {
        LOGGER.fine("Validating pom file content.");
        final JavaMavenGitBranchContent branchContent = (JavaMavenGitBranchContent) this.repository
                .getRepositoryContent(branchName);
        final MavenPom mavenPom = branchContent.getMavenPom();
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