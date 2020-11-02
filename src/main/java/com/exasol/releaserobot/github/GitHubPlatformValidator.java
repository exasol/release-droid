package com.exasol.releaserobot.github;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.releaserobot.AbstractPlatformValidator;
import com.exasol.releaserobot.report.ValidationResult;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.ReleaseLetter;

/**
 * This class checks if the project is ready for a release on GitHub.
 */
public class GitHubPlatformValidator extends AbstractPlatformValidator {
    protected static final String GITHUB_WORKFLOW_PATH = ".github/workflows/github_release.yml";
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    private final GithubGateway githubGateway;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param branchContent content of a branch to validate
     * @param githubGateway instance of {@link GithubGateway}
     */
    public GitHubPlatformValidator(final GitBranchContent branchContent, final GithubGateway githubGateway) {
        super(branchContent);
        this.githubGateway = githubGateway;
    }

    @Override
    public List<ValidationResult> validate() {
        LOGGER.fine("Validating GitHub-specific requirements.");
        final String version = this.branchContent.getVersion();
        final ReleaseLetter releaseLetter = this.branchContent.getReleaseLetter(version);
        validateChangesFile(releaseLetter);
        validateFileExists(GITHUB_WORKFLOW_PATH, "Workflow for a GitHub release.");
        return this.validationResults;
    }

    // [impl->dsn~validate-release-letter~1]
    private void validateChangesFile(final ReleaseLetter releaseLetter) {
        validateContainsHeader(releaseLetter);
        validateGitHubTickets(releaseLetter);
    }

    protected void validateContainsHeader(final ReleaseLetter changes) {
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty()) {
            this.validationResults.add(ValidationResult.failedValidation("E-RR-VAL-1",
                    "The file '" + changes.getFileName()
                            + "' does not contain 'Code name' section which is used as a GitHub release header."
                            + " Please, add this section to the file."));
        } else {
            this.validationResults.add(ValidationResult.successfulValidation("Release letter header."));
        }
    }

    // [impl->dsn~validate-github-issues-exists~1]
    // [impl->dsn~validate-github-issues-are-closed~1]
    protected void validateGitHubTickets(final ReleaseLetter changesFile) {
        try {
            final List<String> wrongTickets = collectWrongTickets(changesFile);
            if (!wrongTickets.isEmpty()) {
                reportWrongTickets(changesFile.getFileName(), wrongTickets);
            } else {
                this.validationResults.add(ValidationResult.successfulValidation("Mentioned GitHub tickets."));
            }
        } catch (final GitHubException exception) {
            this.validationResults.add(ValidationResult.failedValidation("E-RR-VAL-3",
                    "Unable to retrieve a a list of closed tickets on GitHub:" + exception.getMessage()));
        }

    }

    private void reportWrongTickets(final String fileName, final List<String> wrongTickets) {
        final String wrongTicketsString = String.join(", ", wrongTickets);
        if (this.branchContent.isDefaultBranch()) {
            this.validationResults.add(ValidationResult.failedValidation("E-RR-VAL-2",
                    "Some of the mentioned GitHub issues are not closed or do not exists: " + wrongTicketsString
                            + ", Please, check the issues numbers in your '" + fileName + "' one more time."));
        } else {
            final String warningMessage = "W-RR-VAL-1. Don't forget to close the tickets mentioned in the '" + fileName
                    + "' file before you release: " + wrongTicketsString + ".";
            this.validationResults.add(ValidationResult
                    .successfulValidation("Skipping mentioned GitHub tickets validation. " + warningMessage));
            LOGGER.warning(warningMessage);
        }
    }

    private List<String> collectWrongTickets(final ReleaseLetter changesFile) throws GitHubException {
        final Set<Integer> closedTickets = this.githubGateway.getClosedTickets();
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