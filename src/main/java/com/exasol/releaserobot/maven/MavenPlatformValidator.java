package com.exasol.releaserobot.maven;

import com.exasol.releaserobot.AbstractPlatformValidator;
import com.exasol.releaserobot.report.Report;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.maven.JavaMavenGitBranchContent;
import com.exasol.releaserobot.repository.maven.MavenPom;

/**
 * This class checks if the project is ready for a release on Maven Central.
 */
public class MavenPlatformValidator extends AbstractPlatformValidator {
    protected static final String MAVEN_WORKFLOW_PATH = ".github/workflows/maven_central_release.yml";

    /**
     * Create a new instance of {@link MavenPlatformValidator}.
     *
     * @param branchContent content of a branch to validate
     */
    public MavenPlatformValidator(final GitBranchContent branchContent) {
        super(branchContent);
    }

    @Override
    public Report validate() {
        validateFileExists(MAVEN_WORKFLOW_PATH, "Workflow for a Maven release.");
        validateMavenPom(((JavaMavenGitBranchContent) this.branchContent).getMavenPom());
        return this.report;
    }

    // TODO: add a pom file validation
    // https://github.com/exasol/release-robot/issues/50
    private void validateMavenPom(final MavenPom mavenPom) {
    }
}