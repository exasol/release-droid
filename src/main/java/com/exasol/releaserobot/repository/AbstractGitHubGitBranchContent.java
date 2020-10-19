package com.exasol.releaserobot.repository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.github.*;

/**
 * Contains common logic for GitHub-based repositories' content.
 */
public abstract class AbstractGitHubGitBranchContent implements GitBranchContent {
    private static final String CHANGELOG_FILE_PATH = "doc/changes/changelog.md";
    private final GHRepository repository;
    private final GHBranch branch;
    private final Map<String, ReleaseLetter> releaseLetters = new HashMap<>();

    /**
     * Create a new instance of {@link AbstractGitHubGitBranchContent}.
     *
     * @param repository an instance of {@link GHRepository}
     * @param branchName name of a branch to get content from
     */
    protected AbstractGitHubGitBranchContent(final GHRepository repository, final String branchName) {
        this.repository = repository;
        this.branch = getBranchByName(branchName);
    }

    private GHBranch getBranchByName(final String branchName) {
        try {
            return this.repository.getBranch(branchName);
        } catch (final IOException exception) {
            throw new GitRepositoryException("E-REP-GH-1: Cannot find a branch '" + branchName
                    + "'. Please check if you specified a correct branch.", exception);
        }
    }

    @Override
    public String getSingleFileContentAsString(final String filePath) {
        try {
            final GHContent content = this.repository.getFileContent(filePath, this.branch.getName());
            return getContent(content.read());
        } catch (final IOException exception) {
            throw new GitRepositoryException(
                    "E-REP-GH-2: Cannot find or read the file '" + filePath + "' in the repository "
                            + this.repository.getName() + ". Please add this file according to the User Guide.",
                    exception);
        }
    }

    private String getContent(final InputStream stream) throws IOException {
        final StringBuilder result = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                result.append("\n");
                line = reader.readLine();
            }
        }
        return result.toString().stripTrailing();
    }

    @Override
    public boolean isDefaultBranch() {
        return this.repository.getDefaultBranch().equals(this.branch.getName());
    }

    @Override
    public final String getChangelogFile() {
        return getSingleFileContentAsString(CHANGELOG_FILE_PATH);
    }

    @Override
    public final synchronized ReleaseLetter getReleaseLetter(final String version) {
        if (!this.releaseLetters.containsKey(version)) {
            final String fileName = "changes_" + version + ".md";
            final String filePath = "doc/changes/" + fileName;
            final String fileContent = getSingleFileContentAsString(filePath);
            this.releaseLetters.put(version, new ReleaseLetterParser(fileName, fileContent).parse());
        }
        return this.releaseLetters.get(version);
    }
}