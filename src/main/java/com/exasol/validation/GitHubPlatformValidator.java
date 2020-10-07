package com.exasol.validation;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.github.GitHubException;
import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitBranchContent;
import com.exasol.repository.ReleaseLetter;

/**
 * This class checks if the project is ready for a release on GitHub.
 */
public class GitHubPlatformValidator implements PlatformValidator {
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    protected static final String GITHUB_WORKFLOW_PATH = ".github/workflows/github_release.yml";
    private final GitHubPlatform gitHubPlatform;
    private final GitBranchContent branchContent;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param branchContent content of a branch to validate
     * @param gitHubPlatform instance of {@link GitHubPlatform}
     */
    public GitHubPlatformValidator(final GitBranchContent branchContent, final GitHubPlatform gitHubPlatform) {
        this.gitHubPlatform = gitHubPlatform;
        this.branchContent = branchContent;
    }

    @Override
    public void validate() {
        LOGGER.fine("Validating GitHub-specific requirements.");
        final String version = this.branchContent.getVersion();
        final ReleaseLetter releaseLetter = this.branchContent.getReleaseLetter(version);
        validateChangesFile(releaseLetter);
        validateWorkflowFileExists();
    }

    /**
     * Check that the workflow file exists and is reachable.
     */
    protected void validateWorkflowFileExists() {
        try {
            this.branchContent.getSingleFileContentAsString(GITHUB_WORKFLOW_PATH);
        } catch (final GitHubException exception) {
            throw new IllegalStateException("E-RR-VAL-3: '" + GITHUB_WORKFLOW_PATH
                    + "' file does not exist in the project. Please, add this file to release on the GitHub.");
        }
    }

    // [impl->dsn~validate-release-letter~1]
    private void validateChangesFile(final ReleaseLetter releaseLetter) {
        validateContainsHeader(releaseLetter);
        validateGitHubTickets(releaseLetter);
    }

    protected void validateContainsHeader(final ReleaseLetter changes) {
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty()) {
            throw new IllegalStateException("E-RR-VAL-1: The " + changes.getFileName()
                    + " file does not contain 'Code name' section which is used as a GitHub release header."
                    + " Please, add this section to the file");
        }
    }

    // [impl->dsn~validate-github-issues-exists~1]
    // [impl->dsn~validate-github-issues-are-closed~1]
    protected void validateGitHubTickets(final ReleaseLetter changesFile) {
        final List<String> wrongTickets = collectWrongTickets(changesFile);
        if (!wrongTickets.isEmpty()) {
            reportWrongTickets(changesFile.getFileName(), wrongTickets);
        }
    }

    private void reportWrongTickets(final String fileName, final List<String> wrongTickets) {
        final String wrongTicketsString = String.join(", ", wrongTickets);
        if (this.branchContent.isDefaultBranch()) {
            throw new IllegalStateException(
                    "E-RR-VAL-2: Some of the mentioned GitHub issues are not closed or do not exists: "
                            + wrongTicketsString + ", Please, check the issues numbers in your '" + fileName
                            + "' one more time.");
        } else {
            LOGGER.warning("Don't forget to close the tickets mentioned in the '" + fileName
                    + "' file before you release: " + wrongTicketsString);
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