package com.exasol.releaserobot.validation;

import com.exasol.releaserobot.report.ValidationReport;
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
     * @param branchContent    content of a branch to validate
     * @param validationReport instance of {@link ValidationReport}
     */
    public MavenPlatformValidator(final GitBranchContent branchContent, final ValidationReport validationReport) {
        super(branchContent, validationReport);
    }

    @Override
    public void validate() {
        validateFileExists(MAVEN_WORKFLOW_PATH, "Workflow for a Maven release.");
        validateMavenPom(((JavaMavenGitBranchContent) this.branchContent).getMavenPom());
    }

    // TODO: add a pom file validation
    // https://github.com/exasol/release-robot/issues/50
    private void validateMavenPom(final MavenPom mavenPom) {
    }
}