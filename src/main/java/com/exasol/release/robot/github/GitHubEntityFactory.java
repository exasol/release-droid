package com.exasol.release.robot.github;

import static com.exasol.release.robot.Platform.PlatformName.GITHUB;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.exasol.release.robot.CredentialsProvider;
import com.exasol.release.robot.repository.GitHubGitRepository;

/**
 * Responsible for an instantiation of the GitHub related classes.
 */
public final class GitHubEntityFactory {
    private final GHRepository repository;
    private final GitHubUser user;

    /**
     * Create a new instance of {@link GitHubEntityFactory}
     * 
     * @param repositoryOwner owner of the GitHub repository
     * @param repositoryName  name of the GitHubRepository
     */
    public GitHubEntityFactory(final String repositoryOwner, final String repositoryName) {
        this.user = getUser();
        this.repository = getGhRepository(repositoryOwner, repositoryName, this.user);
    }

    /**
     * Create a new instance of {@link GitHubPlatform}.
     * 
     * @return new instance of {@link GitHubPlatform}
     */
    public GitHubPlatform createGitHubPlatform() {
        return new GitHubPlatform(GITHUB, this.repository, this.user);
    }

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     *
     * @return new instance of {@link GitHubGitRepository}
     */
    public GitHubGitRepository createGitHubGitRepository() {
        return new GitHubGitRepository(this.repository);
    }

    private GitHubUser getUser() {
        return CredentialsProvider.getInstance().provideGitHubUserWithCredentials();
    }

    private GHRepository getGhRepository(final String owner, final String name, final GitHubUser user) {
        return createGHRepository(owner, name, user);
    }

    private GHRepository createGHRepository(final String repositoryOwner, final String repositoryName,
            final GitHubUser user) {
        try {
            final GitHub gitHub = GitHub.connect(user.getUsername(), user.getToken());
            return gitHub.getRepository(repositoryOwner + "/" + repositoryName);
        } catch (final IOException exception) {
            throw wrapGitHubException(repositoryName, exception);
        }
    }

    private GitHubException wrapGitHubException(final String repositoryName, final IOException exception) {
        final String originalMessage = exception.getMessage();
        final String newMessage;
        if (originalMessage.contains("Not Found")) {
            newMessage = "Repository '" + repositoryName
                    + "' not found. The repository doesn't exist or the user doesn't have permissions to see it.";
        } else if (originalMessage.contains("Bad credentials")) {
            newMessage = "A GitHub account with specified username and password doesn't exist.";
        } else {
            newMessage = originalMessage;
        }
        return new GitHubException("E-GH-1: " + newMessage, exception);
    }
}