package com.exasol.platform;

import java.io.IOException;
import java.util.Optional;

import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a github repository.
 */
public class GitHubRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubRepository.class);
    private final GHRepository repository;

    private GitHubRepository(final GHRepository repository) {
        this.repository = repository;
    }

    /**
     * Create an instance of {@link GitHubRepository} only for reading purposes. Only for validation of public
     * repositories.
     * 
     * @param repositoryOwner name of the owner on github
     * @param repositoryName name of the repository on github
     * @return new instance of {@link GitHubRepository}
     */
    public static GitHubRepository getAnonymousGitHubRepository(final String repositoryOwner,
            final String repositoryName) {
        final GitHub github = GitHubInstance.getAnonymousGitHub();
        try {
            final GHRepository ghRepository = github.getRepository(repositoryOwner + "/" + repositoryName);
            return new GitHubRepository(ghRepository);
        } catch (final IOException exception) {
            LOGGER.info(
                    "Repository '{}' not found in the list of public repositories of the owner '{}'. "
                            + "Log in as a GitHub user to search in the private repositories too.",
                    repositoryName, repositoryOwner);
            return getLogInGitHubRepository(repositoryOwner, repositoryName);
        }
    }

    /**
     * Create an instance of {@link GitHubRepository}.
     *
     * @param repositoryOwner name of the owner on github
     * @param repositoryName name of the repository on github
     * @return new instance of {@link GitHubRepository}
     */
    public static GitHubRepository getLogInGitHubRepository(final String repositoryOwner, final String repositoryName) {
        final GitHub github = GitHubInstance.getUserVerifiedGitHub();
        try {
            final GHRepository ghRepository = github.getRepository(repositoryOwner + "/" + repositoryName);
            return new GitHubRepository(ghRepository);
        } catch (final IOException exception) {
            throw new IllegalArgumentException("Repository '" + repositoryName
                    + "' not found. The repository doesn't exist or the user doesn't have privileges to see it.");
        }
    }

    /**
     * Get latest released tag if exists.
     * 
     * @return optional-wrapped release tag as a String or empty optional
     */
    public Optional<String> getLatestReleaseVersion() {
        try {
            final GHRelease release = this.repository.getLatestRelease();
            if (release == null) {
                return Optional.empty();
            } else {
                return Optional.of(release.getTagName());
            }
        } catch (final IOException exception) {
            return Optional.empty();
        }
    }

    /**
     * Get a content of any file of this repository.
     * 
     * @param filePath path of the file as a String
     * @return content as a string
     */
    public String getSingleFileContentAsString(final String filePath) {
        try {
            final GHContent content = this.repository.getFileContent(filePath);
            return content.getContent();
        } catch (final IOException exception) {
            throw new IllegalArgumentException("Cannot find or read file " + filePath + " in the repository "
                    + this.repository.getName() + ". Please add this file according to the User Guide.");
        }
    }

    public void release() {
    }
}