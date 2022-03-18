package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_CONFIG_PATH;

import java.util.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.RepositoryException;

/**
 * Base implementation of repository.
 */
public abstract class BaseRepository implements Repository {
    public static final String CHANGELOG_FILE_PATH = "doc/changes/changelog.md";

    private final Map<String, ReleaseLetter> releaseLetters = new HashMap<>();
    private final RepositoryGate repositoryGate;

    /**
     * Create a new instance of {@link BaseRepository}.
     *
     * @param repositoryGate instance of {@link RepositoryGate}
     */
    protected BaseRepository(final RepositoryGate repositoryGate) {
        this.repositoryGate = repositoryGate;
    }

    @Override
    public Optional<ReleaseConfig> getReleaseConfig() {
        try {
            return Optional.of(ReleaseConfigParser.parse(getSingleFileContentAsString(RELEASE_CONFIG_PATH)));
        } catch (final RepositoryException exception) {
            return Optional.empty();
        }
    }

    @Override
    public String getChangelogFile() {
        return getSingleFileContentAsString(CHANGELOG_FILE_PATH);
    }

    @Override
    public ReleaseLetter getReleaseLetter(final String version) {
        this.releaseLetters.computeIfAbsent(version, releaseLetter -> {
            final String fileName = "changes_" + version + ".md";
            final String filePath = "doc/changes/" + fileName;
            final String fileContent = getSingleFileContentAsString(filePath);
            return new ReleaseLetterParser(fileName, fileContent).parse();
        });
        return this.releaseLetters.get(version);
    }

    @Override
    public String getName() {
        return this.repositoryGate.getName();
    }

    @Override
    public Optional<String> getLatestTag() {
        return this.repositoryGate.getLatestTag();
    }

    @Override
    public String getSingleFileContentAsString(final String filePath) {
        return this.repositoryGate.getSingleFileContentAsString(filePath);
    }

    @Override
    public boolean hasFile(final String filePath) {
        return this.repositoryGate.hasFile(filePath);
    }

    @Override
    public void updateFileContent(final String filePath, final String newContent, final String commitMessage) {
        this.repositoryGate.updateFileContent(filePath, newContent, commitMessage);
    }

    @Override
    public boolean isOnDefaultBranch() {
        return this.repositoryGate.isOnDefaultBranch();
    }

    @Override
    public String getBranchName() {
        return this.repositoryGate.getBranchName();
    }

    protected String getVersionFromChangelogFile() {
        final String changelogFile = getChangelogFile();
        final int from = changelogFile.indexOf('[');
        final int to = changelogFile.indexOf(']');
        if (from == -1 || to == -1 || to < from) {
            throw new RepositoryException(ExaError.messageBuilder("E-RD-REP-21")
                    .message("Cannot detect the version of the project.")
                    .mitigation("Please make sure you specified the version in the changelog.md file").toString());
        }
        return changelogFile.substring(from + 1, to);
    }
}