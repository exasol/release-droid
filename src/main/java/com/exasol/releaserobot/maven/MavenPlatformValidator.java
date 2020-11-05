package com.exasol.releaserobot.maven;

import com.exasol.releaserobot.repository.Repository;
import com.exasol.releaserobot.repository.maven.JavaMavenGitBranchContent;
import com.exasol.releaserobot.repository.maven.MavenPom;
import com.exasol.releaserobot.usecases.Report;
import com.exasol.releaserobot.usecases.ReportImpl;
import com.exasol.releaserobot.usecases.validate.AbstractPlatformValidator;

/**
 * This class checks if the project is ready for a release on Maven Central.
 */
public class MavenPlatformValidator extends AbstractPlatformValidator {
    protected static final String MAVEN_WORKFLOW_PATH = ".github/workflows/maven_central_release.yml";

    @Override
    public Report validate(final Repository repository) {
        final Report report = ReportImpl.validationReport();
        report.merge(validateFileExists(repository.getBranch(), MAVEN_WORKFLOW_PATH, "Workflow for a Maven release."));
        report.merge(validateMavenPom(((JavaMavenGitBranchContent) repository.getBranch()).getMavenPom()));
        return report;
    }

    // TODO: add a pom file validation
    // https://github.com/exasol/release-robot/issues/50
    private Report validateMavenPom(final MavenPom mavenPom) {
        return ReportImpl.validationReport();
    }
}