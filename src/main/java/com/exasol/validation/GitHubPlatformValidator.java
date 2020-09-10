package com.exasol.validation;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitBranchContent;
import com.exasol.repository.ReleaseLetter;

/**
 * This class checks if the project is ready for a release on GitHub.
 */
public class GitHubPlatformValidator implements PlatformValidator {
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    private final GitHubPlatform gitHubPlatform;
    private final GitBranchContent repositoryContent;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param repositoryContent content of a repository to validate
     * @param gitHubPlatform instance of {@link GitHubPlatform}
     */
    public GitHubPlatformValidator(final GitBranchContent repositoryContent, final GitHubPlatform gitHubPlatform) {
        this.gitHubPlatform = gitHubPlatform;
        this.repositoryContent = repositoryContent;
    }

    @Override
    public void validate() {
        LOGGER.fine("Validating GitHub-specific requirements.");
        final String version = this.repositoryContent.getVersion();
        final ReleaseLetter releaseLetter = this.repositoryContent.getReleaseLetter(version);
        validateChangesFile(releaseLetter);
    }

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

    protected void validateGitHubTickets(final ReleaseLetter changesFile) {
        final List<String> wrongTickets = collectWrongTickets(changesFile);
        if (!wrongTickets.isEmpty()) {
            throw new IllegalStateException(
                    "E-RR-VAL-2: Some of the mentioned GitHub issues are not closed or do not exists: "
                            + String.join(", ", wrongTickets) + ", Please, check the issues numbers in your '"
                            + changesFile.getFileName() + "' one more time.");
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