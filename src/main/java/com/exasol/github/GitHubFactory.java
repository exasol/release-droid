package com.exasol.github;

import static com.exasol.Platform.PlatformName.GITHUB;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.exasol.CredentialsProvider;
import com.exasol.repository.GitHubGitRepository;

/**
 * Responsible for an instantiation of the GitHub related classes.
 */
public final class GitHubFactory {
    private final String repositoryOwner;
    private final String repositoryName;
    private GHRepository repository;
    private GitHubUser user;

    /**
     * Create a new instance of {@link GitHubFactory}
     * 
     * @param repositoryOwner owner of the GitHub repository
     * @param repositoryName name of the GitHubRepository
     */
    public GitHubFactory(final String repositoryOwner, final String repositoryName) {
        this.repositoryOwner = repositoryOwner;
        this.repositoryName = repositoryName;
    }

    /**
     * Create a new instance of {@link GitHubPlatform}.
     * 
     * @return new instance of {@link GitHubPlatform}
     */
    public GitHubPlatform createGitHubPlatform() {
        return new GitHubPlatform(GITHUB, getGhRepository(), getUser());
    }

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     *
     * @return new instance of {@link GitHubGitRepository}
     */
    public GitHubGitRepository createGitHubGitRepository() {
        return new GitHubGitRepository(getGhRepository());
    }

    // Stores user to avoid reading credentials twice.
    private synchronized GitHubUser getUser() {
        if (this.user == null) {
            this.user = CredentialsProvider.getInstance().provideGitHubUserWithCredentials();
        }
        return this.user;
    }

    // Stores repository to avoid creating multiple connections to the GitHub.
    private synchronized GHRepository getGhRepository() {
        if (this.repository == null) {
            this.repository = createGHRepository(this.repositoryOwner, this.repositoryName, getUser());
        }
        return this.repository;
    }

    private GHRepository createGHRepository(final String repositoryOwner, final String repositoryName,
            final GitHubUser user) {
        try {
            final GitHub gitHub = GitHub.connect(user.getUsername(), user.getToken());
            return gitHub.getRepository(repositoryOwner + "/" + repositoryName);
        } catch (final IOException exception) {
            final String message = getNewExceptionMessage(repositoryName, exception.getMessage());
            throw new GitHubException(message, exception);
        }
    }

    private String getNewExceptionMessage(final String repositoryName, final String oldMessage) {
        if (oldMessage.contains("Not Found")) {
            return "Repository '" + repositoryName
                    + "' not found. The repository doesn't exist or the user doesn't have permissions to see it.";
        } else if (oldMessage.contains("Bad credentials")) {
            return "A GitHub account with specified username and password doesn't exist.";
        } else {
            return oldMessage;
        }
    }
}