package com.exasol.releaserobot.validation;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.releaserobot.github.GitHubPlatform;
import com.exasol.releaserobot.report.ValidationReport;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.ReleaseLetter;

/**
 * This class checks if the project is ready for a release on GitHub.
 */
public class GitHubPlatformValidator extends AbstractPlatformValidator {
    protected static final String GITHUB_WORKFLOW_PATH = ".github/workflows/github_release.yml";
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    private final GitHubPlatform gitHubPlatform;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     * 
     * @param branchContent    content of a branch to validate
     * @param gitHubPlatform   instance of {@link GitHubPlatform}
     * @param validationReport instance of {@link ValidationReport}
     */
    public GitHubPlatformValidator(final GitBranchContent branchContent, final GitHubPlatform gitHubPlatform,
            final ValidationReport validationReport) {
        super(branchContent, validationReport);
        this.gitHubPlatform = gitHubPlatform;
    }

    @Override
    public void validate() {
        LOGGER.fine("Validating GitHub-specific requirements.");
        final String version = this.branchContent.getVersion();
        final ReleaseLetter releaseLetter = this.branchContent.getReleaseLetter(version);
        validateChangesFile(releaseLetter);
        validateFileExists(GITHUB_WORKFLOW_PATH, "Workflow for a GitHub release.");
    }

    // [impl->dsn~validate-release-letter~1]
    private void validateChangesFile(final ReleaseLetter releaseLetter) {
        validateContainsHeader(releaseLetter);
        validateGitHubTickets(releaseLetter);
    }

    protected void validateContainsHeader(final ReleaseLetter changes) {
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty()) {
            this.validationReport.addFailedValidations("E-RR-VAL-1",
                    "The file '" + changes.getFileName()
                            + "' does not contain 'Code name' section which is used as a GitHub release header."
                            + " Please, add this section to the file.");
        } else {
            this.validationReport.addSuccessfulValidation("Release letter header.");
        }
    }

    // [impl->dsn~validate-github-issues-exists~1]
    // [impl->dsn~validate-github-issues-are-closed~1]
    protected void validateGitHubTickets(final ReleaseLetter changesFile) {
        final List<String> wrongTickets = collectWrongTickets(changesFile);
        if (!wrongTickets.isEmpty()) {
            reportWrongTickets(changesFile.getFileName(), wrongTickets);
        } else {
            this.validationReport.addSuccessfulValidation("Mentioned GitHub tickets.");
        }
    }

    private void reportWrongTickets(final String fileName, final List<String> wrongTickets) {
        final String wrongTicketsString = String.join(", ", wrongTickets);
        if (this.branchContent.isDefaultBranch()) {
            this.validationReport.addFailedValidations("E-RR-VAL-2",
                    "Some of the mentioned GitHub issues are not closed or do not exists: " + wrongTicketsString
                            + ", Please, check the issues numbers in your '" + fileName + "' one more time.");
        } else {
            final String warningMessage = "W-RR-VAL-1. Don't forget to close the tickets mentioned in the '" + fileName
                    + "' file before you release: " + wrongTicketsString + ".";
            this.validationReport
                    .addSuccessfulValidation("Skipping mentioned GitHub tickets validation. " + warningMessage);
            LOGGER.warning(warningMessage);
        }
    }

    private List<String> collectWrongTickets(final ReleaseLetter changesFile) {
        final Set<Integer> closedTickets = this.gitHubPlatform.getClosedTickets();
        final List<Integer> mentionedTickets = changesFile.getTicketNumbers();
        final List<String> wrongTickets = new ArrayList<>();
        for (final Integer ticket : mentionedTickets) {
            if (!closedTickets.contains(ticket)) {
                wrongTickets.add(String.valueOf(ticket));
            }
        }
        return wrongTickets;
    }
}