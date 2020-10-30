package com.exasol.releaserobot.github;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.exasol.releaserobot.CredentialsProvider;
import com.exasol.releaserobot.github.release.GitHubReleaseMaker;
import com.exasol.releaserobot.maven.MavenPlatform;
import com.exasol.releaserobot.maven.release.MavenReleaseMaker;
import com.exasol.releaserobot.repository.GitBranchContent;
import com.exasol.releaserobot.repository.GitHubGitRepository;

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
    public GitHubEntityFactory(final String repositoryOwner, final String repositoryName) throws GitHubException {
        this.user = getUser();
        this.repository = getGhRepository(repositoryOwner, repositoryName, this.user);
    }

    /**
     * Create a new instance of {@link GitHubPlatform}.
     * @param content 
     * 
     * @return new instance of {@link GitHubPlatform}
     */
    public GitHubPlatform createGitHubPlatform(final GitBranchContent content) {
    	final GithubGateway githubGateway = new GithubAPIAdapter(this.repository, this.user);
		return new GitHubPlatform(new GitHubReleaseMaker(content, githubGateway), githubGateway);
    }

    /**
     * Create a new instance of {@link MavenPlatform}.
     *
     * @return new instance of {@link MavenPlatform}
     */
    public MavenPlatform createMavenPlatform(final GitBranchContent content) {
    	final GithubGateway githubGateway = new GithubAPIAdapter(this.repository, this.user);
        return new MavenPlatform(new MavenReleaseMaker(content, githubGateway));
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

    private GHRepository getGhRepository(final String owner, final String name, final GitHubUser user)
            throws GitHubException {
        return createGHRepository(owner, name, user);
    }

    private GHRepository createGHRepository(final String repositoryOwner, final String repositoryName,
            final GitHubUser user) throws GitHubException {
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