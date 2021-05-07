package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.CHANGELOG_FILE_PATH;
import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_CONFIG_PATH;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.exasol.releasedroid.usecases.exception.RepositoryException;

/**
 * Base implementation of repository.
 */
public abstract class BaseRepository implements Repository {
    private final Map<String, ReleaseLetter> releaseLetters = new HashMap<>();
    private ReleaseConfig releaseConfig;
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
        if (this.releaseConfig == null) {
            try {
                this.releaseConfig = ReleaseConfigParser.parse(getSingleFileContentAsString(RELEASE_CONFIG_PATH));
            } catch (final RepositoryException exception) {
                this.releaseConfig = null;
            }
        }
        return Optional.ofNullable(this.releaseConfig);
    }

    @Override
    public String getChangelogFile() {
        return getSingleFileContentAsString(CHANGELOG_FILE_PATH);
    }

    @Override
    public ReleaseLetter getReleaseLetter(final String version) {
        if (!this.releaseLetters.containsKey(version)) {
            final String fileName = "changes_" + version + ".md";
            final String filePath = "doc/changes/" + fileName;
            final String fileContent = getSingleFileContentAsString(filePath);
            this.releaseLetters.put(version, new ReleaseLetterParser(fileName, fileContent).parse());
        }
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
}