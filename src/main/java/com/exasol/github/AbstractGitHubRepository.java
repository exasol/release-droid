package com.exasol.github;

import java.io.IOException;
import java.util.*;

import org.kohsuke.github.*;

/**
 * An abstract base of {@link GitHubRepository}.
 */
public abstract class AbstractGitHubRepository implements GitHubRepository {
    private static final String CHANGELOG_FILE_PATH = "doc/changes/changelog.md";
    private final GHRepository repository;
    protected Map<String, String> filesCache = new HashMap<>();

    /**
     * A base constructor.
     * 
     * @param repository an instance of {@link GHRepository}
     */
    protected AbstractGitHubRepository(final GHRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<String> getLatestReleaseVersion() {
        try {
            final GHRelease release = this.repository.getLatestRelease();
            return release == null ? Optional.empty() : Optional.of(release.getTagName());
        } catch (final IOException exception) {
            throw new GitHubException("GitHub connection problem happened during retrieving the latest release. "
                    + "Please, try again later. Cause: " + exception.getMessage(), exception);
        }
    }

    /**
     * Get a content of any file of this repository.
     *
     * @param filePath path of the file as a String
     * @return content as a string
     */
    protected String getSingleFileContentAsString(final String filePath) {
        try {
            final GHContent content = this.repository.getFileContent(filePath);
            return content.getContent();
        } catch (final IOException exception) {
            throw new GitHubException(
                    "Cannot find or read the file '" + filePath + "' in the repository " + this.repository.getName()
                            + ". Please add this file according to the User Guide. Cause: " + exception.getMessage(),
                    exception);
        }
    }

    @Override
    public String getChangelogFile() {
        if (!this.filesCache.containsKey(CHANGELOG_FILE_PATH)) {
            this.filesCache.put(CHANGELOG_FILE_PATH, getSingleFileContentAsString(CHANGELOG_FILE_PATH));
        }
        return this.filesCache.get(CHANGELOG_FILE_PATH);
    }

    @Override
    public String getChangesFile() {
        final String changesFileName = "doc/changes/changes-" + getVersion() + ".md";
        if (!this.filesCache.containsKey(changesFileName)) {
            this.filesCache.put(changesFileName, getSingleFileContentAsString(changesFileName));
        }
        return this.filesCache.get(changesFileName);
    }

    @Override
    public void release(final String tag, final String name, final String releaseLetter) {
        try {
            this.repository.createRelease(tag).draft(true).body(releaseLetter).name(name).create();
        } catch (final IOException exception) {
            throw new GitHubException("GitHub connection problem happened during releasing a new tag. "
                    + "Please, try again later. Cause: " + exception.getMessage(), exception);
        }
    }
}