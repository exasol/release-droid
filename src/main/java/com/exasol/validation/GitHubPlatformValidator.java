package com.exasol.validation;

import java.util.Optional;
import java.util.logging.Logger;

import com.exasol.git.GitRepositoryContent;
import com.exasol.git.ReleaseChangesLetter;

/**
 * This class checks if the GitHub project repository is ready for a release.
 */
public class GitHubPlatformValidator implements PlatformValidator {
    private static final Logger LOGGER = Logger.getLogger(GitHubPlatformValidator.class.getName());
    private final GitRepositoryContent repositoryContent;

    /**
     * Create a new instance of {@link GitHubPlatformValidator}.
     *
     * @param repositoryContent content of a repository to validate
     */
    public GitHubPlatformValidator(final GitRepositoryContent repositoryContent) {
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

    private void validateContainsHeader(final ReleaseChangesLetter changes) {
        final Optional<String> header = changes.getHeader();
        if (header.isEmpty()) {
            throw new IllegalStateException("The " + changes.getFileName()
                    + " file does not contain 'Code name' section which is used as a GitHub release header."
                    + " Please, add this section to the file");
        }
    }

    private void validateGitHubTickets(final ReleaseChangesLetter changesFile) {

    }
}