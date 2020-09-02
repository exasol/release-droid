package com.exasol.validation;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.github.GitHubPlatform;
import com.exasol.repository.GitRepositoryContent;
import com.exasol.repository.ReleaseChangesLetter;

/**
 * This class checks if the project is ready for a release on GitHub.
 */
public class GitHubPlatformValidator implements PlatformValidator {
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    private final GitHubPlatform gitHubPlatform;
    private final GitRepositoryContent repositoryContent;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param repositoryContent content of a repository to validate
     * @param gitHubPlatform instance of {@link GitHubPlatform}
     */
    public GitHubPlatformValidator(final GitRepositoryContent repositoryContent, final GitHubPlatform gitHubPlatform) {
        this.gitHubPlatform = gitHubPlatform;
        this.repositoryContent = repositoryContent;
    }

    @Override
    public void validate() {
        LOGGER.fine("Validating GitHub-specific requirements.");
        final String version = this.repositoryContent.getVersion();
        final ReleaseChangesLetter changes = this.repositoryContent.getReleaseChangesLetter(version);
        validateChangesFile(changes);
    }

    private void validateChangesFile(final ReleaseChangesLetter changes) {
        validateContainsHeader(changes);
        validateGitHubTickets(changes);
    }

    protected void validateContainsHeader(final ReleaseChangesLetter changes) {
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty()) {
            throw new IllegalStateException("The " + changes.getFileName()
                    + " file does not contain 'Code name' section which is used as a GitHub release header."
                    + " Please, add this section to the file");
        }
    }

    protected void validateGitHubTickets(final ReleaseChangesLetter changesFile) {
        final List<String> wrongTickets = collectWrongTickets(changesFile);
        if (!wrongTickets.isEmpty()) {
            throw new IllegalStateException("Some of the mentioned GitHub issues are not closed or do not exists: "
                    + String.join(", ", wrongTickets) + ", Please, check the issues numbers in your '"
                    + changesFile.getFileName() + "' one more time.");
        }
    }

    private List<String> collectWrongTickets(final ReleaseChangesLetter changesFile) {
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