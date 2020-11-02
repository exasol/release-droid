package com.exasol.releaserobot.github;

import java.util.Set;

import com.exasol.releaserobot.*;

/**
 * This class controls GitHub platform.
 */
public class GitHubPlatform implements Platform {
    private final ReleaseMaker releaseMaker;
    private final GithubGateway githubGateway;

    /**
     * Create a new instance of {@link GitHubPlatform}.
     *
     * @param releaseMaker  instance of {@link ReleaseMaker}
     * @param githubGateway instance of {@link GithubGateway}
     */
    protected GitHubPlatform(final ReleaseMaker releaseMaker, final GithubGateway githubGateway) {
        this.releaseMaker = releaseMaker;
        this.githubGateway = githubGateway;
    }

    @Override
    public void release(final UserInput userInput) throws GitHubException {
        this.releaseMaker.makeRelease();
    }

    /**
     * Get a set of closed issues' numbers.
     *
     * @return set of closed issues' numbers
     */
    public Set<Integer> getClosedTickets() {
        try {
            return this.githubGateway.getClosedTickets();
        } catch (final GitHubException exception) {
            throw new IllegalStateException(exception.getMessage(), exception);
        }
    }

    @Override
    public PlatformName getPlatformName() {
        return PlatformName.GITHUB;
    }
}