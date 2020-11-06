package com.exasol.releaserobot.usecases;

import java.io.*;
import java.util.*;

import org.kohsuke.github.*;

import com.exasol.releaserobot.repository.*;

/**
 * This class represents Git repository content based on the latest commit of the user-specified branch.
 */
public abstract class Repository {
    private static final String CHANGELOG_FILE_PATH = "doc/changes/changelog.md";
    private final GHRepository repository;
    private final GHBranch branch;
    private final Optional<String> latestTag;
    private final Map<String, ReleaseLetter> releaseLetters = new HashMap<>();
    private final String fullName;

    /**
     * Create a new instance of {@link AbstractGitHubGitBranch}.
     *
     * @param repository an instance of {@link GHRepository}
     * @param branchName name of a branch to get content from
     */
    protected Repository(final GHRepository repository, final String branchName) {
        this.repository = repository;
        this.branch = getBranchByName(branchName);
    }

    private GHBranch getBranchByName(final String branchName) {
        try {
            return Repository.repository.getBranch(branchName);
        } catch (final IOException exception) {
            throw new GitRepositoryException("E-REP-GH-3: Cannot find a branch '" + branchName
                    + "'. Please check if you specified a correct branch.", exception);
        }
    }

    /**
     * Get the content of a file in this repository.
     *
     * @param filePath path of the file as a string
     * @return content as a string
     */
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

    /**
     * Check if the branch is the default branch.
     *
     * @return true if the branch is default
     */
    public boolean isDefaultBranch() {
        return this.repository.getDefaultBranch().equals(this.branch.getName());
    }

    /**
     * Get the branch name.
     *
     * @return branch name as a string
     */
    public String getBranchName() {
        return this.branch.getName();
    }

    /**
     * Get a changelog file as a string.
     *
     * @return changelog file as a string
     */
    public final String getChangelogFile() {
        return getSingleFileContentAsString(CHANGELOG_FILE_PATH);
    }

    /**
     * Get a changes file as an instance of {@link ReleaseLetter}.
     *
     * @param version version as a string
     * @return release changes file
     */
    public final synchronized ReleaseLetter getReleaseLetter(final String version) {
        if (!this.releaseLetters.containsKey(version)) {
            final String fileName = "changes_" + version + ".md";
            final String filePath = "doc/changes/" + fileName;
            final String fileContent = getSingleFileContentAsString(filePath);
            this.releaseLetters.put(version, new ReleaseLetterParser(fileName, fileContent).parse());
        }
        return this.releaseLetters.get(version);
    }

    public String getFullName() {
        return this.fullName;
    }

    /**
     * Get a current project version.
     *
     * @return version as a string
     */
    // [impl->dsn~gr-provides-current-version~1]
    public abstract String getVersion();

    /**
     * Get key-value pairs for deliverable names and corresponding deliverable pathes.
     *
     * @return map with deliverables information
     */
    // [impl->dsn~gr-provides-deliverables-information~1]
    public abstract Map<String, String> getDeliverables();

    /**
     * Get the latest tag if exists.
     *
     * @return latest tag as a string or empty optional
     */
    public Optional<String> getLatestTag() {
        return this.latestTag;
    }
}