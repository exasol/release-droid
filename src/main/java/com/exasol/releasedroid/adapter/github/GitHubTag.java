package com.exasol.releasedroid.adapter.github;

import java.io.IOException;

import org.kohsuke.github.*;

import com.exasol.errorreporting.ExaError;

/**
 * Query and manipulate git tags in GitHub repository.
 */
public class GitHubTag {

    private final GHRepository repository;

    /**
     * Create a new instance of {@link GitHubTag}
     *
     * @param repository GitHub repository to query and manipulate git tags in
     */
    public GitHubTag(final GHRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a new git tag for the latest commit on default branch.
     *
     * @param tag tag to create
     * @throws GitHubException on error
     */
    // [impl->dsn~creating-git-tags~1]
    public void create(final String tag) throws GitHubException, LatestCommitException {
        try {
            final String sha = shaOfLatestCommit(this.repository.getDefaultBranch());
            this.repository.createRef("refs/tags/" + tag, sha);
        } catch (final IOException exception) {
            // in case the alias already exists the API will throw an HttpException with message "Reference already
            // exists".
            throw new GitHubException(ExaError.messageBuilder("E-RD-GH-30") //
                    .message("Failed creating additional tag {{tag}}.", tag).toString(), exception);
        }
    }

    private String shaOfLatestCommit(final String branch) {
        try {
            return latestCommit(branch).getSHA1();
        } catch (final IOException | LatestCommitException exception) {
            throw new LatestCommitException(ExaError.messageBuilder("E-RD-GH-32")
                    .message("Failed retrieving latest commit on branch {{branch}}.", branch) //
                    .toString(), (exception instanceof LatestCommitException) ? null : exception);
        }
    }

    private GHCommit latestCommit(final String branch) throws IOException {
        final String sha = this.repository.getRef("refs/heads/" + branch).getObject().getSha();
        final PagedIterator<GHCommit> it = this.repository.queryCommits().from(sha).pageSize(1).list().iterator();
        if (!it.hasNext()) {
            throw new LatestCommitException("");
        }
        return it.next();
    }

    static class LatestCommitException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public LatestCommitException(final String message) {
            super(message);
        }

        public LatestCommitException(final String message, final Exception exception) {
            super(message, exception);
        }
    }

}
