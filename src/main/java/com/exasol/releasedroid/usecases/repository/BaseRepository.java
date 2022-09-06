package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_CONFIG_PATH;

import java.util.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.version.*;
import com.exasol.releasedroid.usecases.repository.version.RevisionParser.ChangelogException;
import com.exasol.releasedroid.usecases.repository.version.RevisionParser.ConfigurationException;

/**
 * Base implementation of repository.
 */
public abstract class BaseRepository implements Repository {

    /**
     * Changelog file used to retrieve current version of the repository.
     */
    static final String CHANGELOG_FILE = "doc/changes/changelog.md";

    /**
     * Configuration file used by <a href="https://github.com/exasol/project-keeper">project-keeper</a> describing the project structure.
     */
    static final String CONFIGURATION_FILE = ".project-keeper.yml";

    private final Map<String, ReleaseLetter> releaseLetters = new HashMap<>();
    private final RepositoryGate repositoryGate;
    private Revision revision;
    private String changelog;
    private String configuration;

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
    public String getChangelog() {
        if (this.changelog == null) {
            this.changelog = getSingleFileContentAsString(CHANGELOG_FILE);
        }
        return this.changelog;
    }

    private String getProjectConfiguration() {
        if (this.configuration == null) {
            this.configuration = hasFile(CONFIGURATION_FILE) //
                    ? getSingleFileContentAsString(CONFIGURATION_FILE)
                    : "";
        }
        return this.configuration;
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
    public Optional<Version> getLatestTag() {
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

    /**
     * Getting the version from changelog is safe since project-keeper validates it. In contrast to getting the version
     * from the pom this approach also works with multimodule projects.
     *
     * @return {@link Revision}
     */
    Revision revision() {
        if (this.revision == null) {
            try {
                this.revision = RevisionParser.parse(getChangelog(), getProjectConfiguration());
            } catch (final ChangelogException e) {
                throw new RepositoryException(ExaError.messageBuilder("E-RD-REP-21")
                        .message("Cannot detect the version of the project.")
                        .mitigation("Please make sure file {{changelog}} contains the current version of the project.",
                                CHANGELOG_FILE)
                        .toString());
            } catch (final ConfigurationException e) {
                throw new RepositoryException(ExaError.messageBuilder("E-RD-REP-33")
                        .message("Failed to read project modules.")
                        .mitigation("Please make sure file {{configuration}} is a valid yml file.", CONFIGURATION_FILE)
                        .toString());
            }
        }
        return this.revision;
    }

    @Override
    public String getVersion() {
        return revision().getVersion();
    }

    @Override
    public List<String> getGitTags() {
        return revision().getTags();
    }
}