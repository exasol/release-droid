package com.exasol.github;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.github.*;

import com.exasol.git.GitRepositoryContent;

/**
 * Contains common logic for GitHub-based repositories' content.
 */
public abstract class AbstractGitHubGitRepositoryContent implements GitRepositoryContent {
    private static final String CHANGELOG_FILE_PATH = "doc/changes/changelog.md";
    protected Map<String, String> filesCache = new HashMap<>();
    private final GHRepository repository;
    private final GHBranch branch;

    /**
     * Create a new instance of {@link AbstractGitHubGitRepositoryContent}.
     *
     * @param repository an instance of {@link GHRepository}
     * @param branch name of a branch to get content from
     */
    protected AbstractGitHubGitRepositoryContent(final GHRepository repository, final String branch) {
        this.repository = repository;
        this.branch = getBranchByName(branch);
    }

    private GHBranch getBranchByName(final String branch) {
        try {
            return this.repository.getBranch(branch);
        } catch (final IOException exception) {
            throw new GitHubException(
                    "Cannot find a branch '" + branch + "'. Please check if you specified a correct branch.",
                    exception);
        }
    }

    /**
     * Get the content of a file in this repository.
     *
     * @param filePath path of the file as a string
     * @return content as a string
     */
    protected String getSingleFileContentAsString(final String filePath) {
        try {
            final GHContent content = this.repository.getFileContent(filePath, this.branch.getName());
            return content.getContent();
        } catch (final IOException exception) {
            throw new GitHubException("Cannot find or read the file '" + filePath + "' in the repository "
                    + this.repository.getName() + ". Please add this file according to the User Guide.", exception);
        }
    }

    @Override
    public final synchronized String getChangelogFile() {
        if (!this.filesCache.containsKey(CHANGELOG_FILE_PATH)) {
            this.filesCache.put(CHANGELOG_FILE_PATH, getSingleFileContentAsString(CHANGELOG_FILE_PATH));
        }
        return this.filesCache.get(CHANGELOG_FILE_PATH);
    }

    @Override
    public final synchronized String getChangesFile(final String version) {
        final String changesFileName = "doc/changes/changes_" + version + ".md";
        if (!this.filesCache.containsKey(changesFileName)) {
            this.filesCache.put(changesFileName, getSingleFileContentAsString(changesFileName));
        }
        return this.filesCache.get(changesFileName);
    }
}