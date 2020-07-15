package com.exasol.platform;

import java.io.IOException;
import java.util.Optional;

import org.kohsuke.github.*;

/**
 * This class represents a github repository.
 */
public class GitHubRepository {
    private final GitHub github;
    private final String owner;
    private final String name;
    private final GHRepository repository;

    private GitHubRepository(final String owner, final String name) {
        try {
            this.github = GitHub.connectAnonymously();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                    "Cannot create GitHubController due to unexpected error: " + exception.getMessage());
        }
        this.owner = owner;
        this.name = name;
        this.repository = getRepository();
    }

    private GHRepository getRepository() {
        try {
            return this.github.getRepository(this.owner + "/" + this.name);
        } catch (final IOException exception) {
            throw new IllegalArgumentException("Repository '" + this.name
                    + "' not found in the list of public repositories of the owner '" + this.owner + "'.");
        }
    }

    /**
     * Create an instance of {@link GitHubRepository} without possibilities to edit it.
     * 
     * @param repositoryOwner name of the owner on github
     * @param repositoryName name of the repository on github
     * @return new instance of {@link GitHubRepository}
     */
    public static GitHubRepository getImmutableGitHubRepository(final String repositoryOwner,
            final String repositoryName) {
        return new GitHubRepository(repositoryOwner, repositoryName);
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
}